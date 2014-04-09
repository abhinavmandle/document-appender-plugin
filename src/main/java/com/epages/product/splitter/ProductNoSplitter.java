package com.epages.product.splitter;

import com.epages.product.Product;
import com.epages.product.SubProduct;

/**
 * Example of a non-splitting product splitter.
 */
public class ProductNoSplitter extends AbstractProductSplitter<String> {

    @Override
    public boolean isSplittable(Product product) {
        return false;
    }

    @Override
    protected String createGroupKey(SubProduct subProduct) {
        return "";
    }

}
