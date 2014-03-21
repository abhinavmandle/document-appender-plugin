package com.epages.partner.documentappender;

import com.epages.index.appender.DocumentAppender;
import com.epages.plugin.AbstractPlugin;
import com.epages.product.reader.AttributeReader;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

public class DocumentAppenderPlugin extends AbstractPlugin {

    @Override
    protected void configure() {
        Multibinder<AttributeReader<?>> attributeBinder = Multibinder.newSetBinder(binder(), new TypeLiteral<AttributeReader<?>>(){});
        attributeBinder.addBinding().to(ProductTypeReader.class);

        Multibinder<DocumentAppender> mainProductOnly = Multibinder.newSetBinder(binder(), DocumentAppender.class, Names.named("mainProductOnly"));
        mainProductOnly.addBinding().to(ProductTypeAppender.class);
    }
}
