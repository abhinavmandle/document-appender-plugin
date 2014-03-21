package com.epages.partner.documentappender;

import javax.inject.Inject;

import org.apache.solr.common.SolrInputDocument;

import com.epages.index.appender.DocumentAppender;
import com.epages.index.appender.DocumentValueAppender;
import com.epages.product.IProduct;
import com.epages.product.attribute.entry.CustomAttribute;
import com.google.inject.name.Named;

public class ProductTypeAppender implements DocumentAppender {

    private final DocumentValueAppender appender;

    @Inject
    public ProductTypeAppender(@Named("single") DocumentValueAppender appender) {
        this.appender = appender;
    }

    @Override
    public void append(SolrInputDocument doc, IProduct product) {
        for (CustomAttribute entry : product.getAllLocalizedCustomAttributes()) {
            if ("ProductType".equals(entry.getKey().getKeyName())) {
                this.appender.append(entry.getKey().getLang()+ "_CustomLocale", entry.getValue().getValue(), doc);
            }
        }
    }

}
