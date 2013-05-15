package com.epages.partner.documentappender;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.inject.Inject;

import org.springframework.jdbc.core.RowMapper;

import com.epages.dao.ProductReaderPreparedStatementSetter;
import com.epages.datasource.api.DataSourceLookup;
import com.epages.metadata.JdbcCursorItemReader4EPJ;
import com.epages.product.Product;
import com.epages.product.attribute.entry.AttributeKey;
import com.epages.product.attribute.entry.AttributeKeyBuilder;
import com.epages.product.attribute.entry.CustomAttributeMapEntry;
import com.epages.product.attribute.entry.CustomAttributeValue;
import com.epages.product.attribute.entry.CustomSearchFilterAttributeType;
import com.epages.product.reader.AttributeValidator;
import com.epages.product.reader.BasePeekableReader;

final class ProductTypeReader extends BasePeekableReader<CustomAttributeValue, CustomAttributeMapEntry> {

    @Inject
    public ProductTypeReader(AttributeValidator validator, DataSourceLookup dataSourceLookup) {
        super(validator, new Reader(dataSourceLookup));
    }

    @Override
    public void addToProduct(Product product, CustomAttributeMapEntry attribute) {
        product.addAttribute(attribute);
    }
    
    static final class Reader extends JdbcCursorItemReader4EPJ<CustomAttributeMapEntry> {

        private static final RowMapper<CustomAttributeMapEntry> ROW_MAPPER =  new RowMapper<CustomAttributeMapEntry>() {

            @Override
            public CustomAttributeMapEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
                AttributeKey key = new AttributeKeyBuilder(rs.getInt("main_productid"), rs.getString("attributekey")).withVariationId(getVariationId(rs)).localised(rs.getString("langcode")).build();
                // TODO: "ProductType" should be localized somehow
                CustomAttributeValue attributeValue = new CustomAttributeValue("ProductType", CustomSearchFilterAttributeType.PreDefLocalizedString, rs.getString("langcode"));
                attributeValue.setAttributeValue(rs.getString("attributevalue"), 0);
                attributeValue.setIssearchfilter(1);
                return new CustomAttributeMapEntry(key, attributeValue);
            }
        };

        public static final String SQL = new StringBuilder()
        .append("SELECT COALESCE(p.superproductid, p.productid) as main_productid,")
        .append(" \"ProductType\" as attributekey, ")
        .append(" l.code2 as langcode, lsa.value as attributevalue, p.superproductid, p.productid ")
        .append(" FROM product p ")
        .append(" JOIN object o on p.productid=o.objectid")
         .append(" JOIN localizedstringattribute lsa on (o.classid = lsa.objectid) ")
         .append(" JOIN attribute a ON lsa.attributeid = a.attributeid AND a.type = 'LocalizedString' ")
         .append(" JOIN language  l ON l.languageid = lsa.languageid ")
         .append(" JOIN object lsao on lsa.attributeid=lsao.objectid")
         .append(" WHERE lsao.alias=\"Name\"")
         .append(" AND p.shopid = ? %s ")
         .append(" ORDER BY main_productid").toString();

        public static final String SHOP_SQL = String.format(SQL, "");

        public static final String PRODUCT_SQL = String.format(SQL, " AND COALESCE(p.superproductid, p.productid) = ?");

        public Reader(DataSourceLookup dataSourceLookup) {
            super(dataSourceLookup, ROW_MAPPER);
        }

        @Override
        public void setReader(String dsName, ProductReaderPreparedStatementSetter pstSetter) {
            super.setReader(dsName, pstSetter, getSQL(pstSetter));
        }

        private static String getSQL(ProductReaderPreparedStatementSetter pstSetter) {
            if (pstSetter.getProductId() > 0) {
                return PRODUCT_SQL;
            }
            return SHOP_SQL;
        }
    }

}
