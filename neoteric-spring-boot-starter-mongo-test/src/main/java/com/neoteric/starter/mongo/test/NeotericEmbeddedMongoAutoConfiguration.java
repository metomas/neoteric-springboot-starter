package com.neoteric.starter.mongo.test;

import com.mongodb.Mongo;
import com.neoteric.starter.Constants;
import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.*;
import de.flapdoodle.embed.mongo.distribution.Feature;
import de.flapdoodle.embed.mongo.distribution.IFeatureAwareVersion;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.io.Processors;
import de.flapdoodle.embed.process.io.Slf4jLevel;
import de.flapdoodle.embed.process.io.progress.Slf4jProgressListener;
import de.flapdoodle.embed.process.runtime.Network;
import de.flapdoodle.embed.process.store.ArtifactStoreBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Configuration
@EnableConfigurationProperties({MongoProperties.class, EmbeddedMongoProperties.class})
@AutoConfigureBefore(EmbeddedMongoAutoConfiguration.class)
@ConditionalOnClass({Mongo.class, MongodStarter.class})
public class NeotericEmbeddedMongoAutoConfiguration {

    private static final String LOCALHOST = "localhost";
    private static final Logger LOG = LoggerFactory.getLogger(NeotericEmbeddedMongoAutoConfiguration.class);

    @Autowired
    private EmbeddedMongoProperties embeddedProperties;

    @Autowired
    private ApplicationContext context;

    @Autowired(required = false)
    private IRuntimeConfig runtimeConfig;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(Logger.class)
    public IRuntimeConfig embeddedMongoRuntimeConfig() {
        Logger logger = LoggerFactory
                .getLogger(getClass().getPackage().getName() + ".EmbeddedMongo");
        ProcessOutput processOutput = new ProcessOutput(
                Processors.logTo(logger, Slf4jLevel.INFO),
                Processors.logTo(logger, Slf4jLevel.ERROR), Processors.named("[console>]",
                Processors.logTo(logger, Slf4jLevel.DEBUG)));
        return new RuntimeConfigBuilder()
                .defaultsWithLogger(Command.MongoD, logger)
                .processOutput(processOutput)
                .artifactStore(getArtifactStore(logger))
                .build();
    }

    private ArtifactStoreBuilder getArtifactStore(Logger logger) {
        return new ExtractedArtifactStoreBuilder()
                .defaults(Command.MongoD)
                .download(new DownloadConfigBuilder()
                        .defaultsForCommand(Command.MongoD)
                        .progressListener(new Slf4jProgressListener(logger)));
    }

    @Bean
    @ConditionalOnMissingBean
    public IMongodConfig embeddedMongoConfiguration() throws IOException {
        IFeatureAwareVersion featureAwareVersion = new ToStringFriendlyFeatureAwareVersion(
                this.embeddedProperties.getVersion(),
                this.embeddedProperties.getFeatures());
        MongodConfigBuilder builder = new MongodConfigBuilder()
                .version(featureAwareVersion);
        int port = Network.getFreeServerPort();
        builder.net(new Net(LOCALHOST, port, Network.localhostIsIPv6()));
        LOG.debug("{}Embedded MongoDB port: {}", Constants.LOG_PREFIX, port);
        return builder
                .timeout(new Timeout(30000))
                .build();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnMissingBean
    public MongodExecutable embeddedMongoServer(IMongodConfig mongodConfig)
            throws IOException {
        publishPortInfo(mongodConfig.net().getPort());
        MongodStarter mongodStarter = getMongodStarter(this.runtimeConfig);
        return mongodStarter.prepare(mongodConfig);
    }

    private MongodStarter getMongodStarter(IRuntimeConfig runtimeConfig) {
        if (runtimeConfig == null) {
            return MongodStarter.getDefaultInstance();
        }
        return MongodStarter.getInstance(runtimeConfig);
    }

    private void publishPortInfo(int port) {
        setPortProperty(this.context, port);
    }

    private void setPortProperty(ApplicationContext currentContext, int port) {
        if (currentContext instanceof ConfigurableApplicationContext) {
            MutablePropertySources sources = ((ConfigurableApplicationContext) currentContext)
                    .getEnvironment().getPropertySources();
            getMongoPorts(sources).put("local.mongo.port", port);
        }
        if (currentContext.getParent() != null) {
            setPortProperty(currentContext.getParent(), port);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getMongoPorts(MutablePropertySources sources) {
        PropertySource<?> propertySource = sources.get("mongo.ports");
        if (propertySource == null) {
            propertySource = new MapPropertySource("mongo.ports", new HashMap<>());
            sources.addFirst(propertySource);
        }
        return (Map<String, Object>) propertySource.getSource();
    }

    /**
     * A workaround for the lack of a {@code toString} implementation on
     * {@code GenericFeatureAwareVersion}.
     */
    private final static class ToStringFriendlyFeatureAwareVersion
            implements IFeatureAwareVersion {

        private final String version;

        private final Set<Feature> features;

        private ToStringFriendlyFeatureAwareVersion(String version,
                                                    Set<Feature> features) {
            Assert.notNull(version, "version must not be null");
            this.version = version;
            this.features = (features == null ? Collections.<Feature>emptySet()
                    : features);
        }

        @Override
        public String asInDownloadPath() {
            return this.version;
        }

        @Override
        public boolean enabled(Feature feature) {
            return this.features.contains(feature);
        }

        @Override
        public String toString() {
            return this.version;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + this.features.hashCode();
            result = prime * result + this.version.hashCode();
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() == obj.getClass()) {
                ToStringFriendlyFeatureAwareVersion other = (ToStringFriendlyFeatureAwareVersion) obj;
                boolean equals = true;
                equals &= this.features.equals(other.features);
                equals &= this.version.equals(other.version);
                return equals;
            }
            return super.equals(obj);
        }
    }
}

