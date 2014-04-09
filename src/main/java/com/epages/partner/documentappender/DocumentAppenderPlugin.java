package com.epages.partner.documentappender;


import com.epages.plugin.AbstractPlugin;
import com.epages.plugin.Feature;
import com.epages.product.reader.AttributeReader;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;

@Feature(value="DocumentAppenderPlugin", dependencies={"Search"})
public class DocumentAppenderPlugin extends AbstractPlugin {

    @Override
    protected void configure() {
        // to enable the non-splitting splitter, disable the feature "ProductSplitter"
        // bind(ProductSplitter.class).to(ProductNoSplitter.class).in(Singleton.class);
        bindToDefaultConfig("conf/document-appender-plugin.conf");

        // example of an additional SQL reader
        Multibinder<AttributeReader<?>> attributeBinder = Multibinder.newSetBinder(binder(), new TypeLiteral<AttributeReader<?>>(){});
        attributeBinder.addBinding().to(ProductTypeReader.class);
        attributeBinder.addBinding().to(LocalizedCustomStringAttributeFacetsReader.class);
    }

}
