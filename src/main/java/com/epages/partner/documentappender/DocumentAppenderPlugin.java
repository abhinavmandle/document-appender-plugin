package com.epages.partner.documentappender;


import javax.inject.Singleton;

import org.apache.commons.configuration.Configuration;

import com.epages.configuration.EPJConfigProvider;
import com.epages.index.appender.DocumentAppender;
import com.epages.plugin.AbstractPlugin;
import com.epages.plugin.Feature;
import com.epages.product.reader.AttributeReader;
import com.epages.product.splitter.ProductNoSplitter;
import com.epages.product.splitter.ProductSplitter;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

@Feature(value="DocumentAppenderPlugin", dependencies={"Search"})
public class DocumentAppenderPlugin extends AbstractPlugin {

    @Override
    protected void configure() {
        bind(ProductSplitter.class).to(ProductNoSplitter.class).in(Singleton.class);
        bindToDefaultConfig(ConfigFileProvider.class);

        Multibinder<AttributeReader<?>> attributeBinder = Multibinder.newSetBinder(binder(), new TypeLiteral<AttributeReader<?>>(){});
        attributeBinder.addBinding().to(ProductTypeReader.class);

        Multibinder<DocumentAppender> mainProductOnly = Multibinder.newSetBinder(binder(), DocumentAppender.class, Names.named("mainProductOnly"));
        mainProductOnly.addBinding().to(ProductTypeAppender.class);
    }

    static class ConfigFileProvider extends EPJConfigProvider {
        @Override
        public Configuration get() {
            return getConfigURI("conf/document-appender-plugin.conf");
        }
    }

}
