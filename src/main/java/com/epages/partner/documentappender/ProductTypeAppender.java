package com.epages.partner.documentappender;

import java.util.Map.Entry;

import javax.inject.Inject;

import org.apache.solr.common.SolrInputDocument;

import com.epages.index.domain.solrj.doc.DocumentAppender;
import com.epages.index.domain.solrj.doc.DocumentValueAppender;
import com.epages.product.IProduct;
import com.epages.product.attribute.entry.CustomAttributeValue;
import com.epages.product.attribute.entry.LocalizedKey;
import com.google.inject.name.Named;

public class ProductTypeAppender implements DocumentAppender {

    private final DocumentValueAppender appender;

    @Inject
    public ProductTypeAppender(@Named("single") DocumentValueAppender appender) {
        this.appender = appender;
    }

    @Override
    public void append(SolrInputDocument doc, IProduct product) {
        for (Entry<LocalizedKey, CustomAttributeValue> entry : product.getAllAttributes(LocalizedKey.class, CustomAttributeValue.class)) {
            if ("ProductType".equals(entry.getKey().getKeyName())) {
                this.appender.append(entry.getKey().getLang()+ "_CustomLocale", entry.getValue().getValue(), doc);
            }
        }
    }

}
