package com.epages.partner.documentappender;

import com.epages.commons.plugin.Plugin;
import com.epages.index.domain.solrj.doc.DocumentAppender;
import com.epages.product.attribute.entry.CustomAttributeMapEntry;
import com.epages.product.attribute.entry.CustomAttributeValue;
import com.epages.product.reader.AttributeReader;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

public class DocumentAppenderPlugin extends AbstractModule implements Plugin {

    @Override
    protected void configure() {
        bind(new TypeLiteral<AttributeReader<CustomAttributeValue, CustomAttributeMapEntry>>() {}).annotatedWith(Names.named("partner-producttype")).to(ProductTypeReader.class);

        Multibinder<AttributeReader<?, ?>> attributeBinder = Multibinder.newSetBinder(binder(), new TypeLiteral<AttributeReader<?, ?>>() {});
        attributeBinder.addBinding().to(ProductTypeReader.class);

        Multibinder<DocumentAppender> mainProductOnly = Multibinder.newSetBinder(binder(), DocumentAppender.class, Names.named("mainProductOnly"));
        mainProductOnly.addBinding().to(ProductTypeAppender.class);
    }
}
