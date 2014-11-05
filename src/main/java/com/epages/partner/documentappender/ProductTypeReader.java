package com.epages.partner.documentappender;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import javax.inject.Inject;

import org.springframework.jdbc.core.RowMapper;

import com.epages.product.ProductReaderPreparedStatementSetter;
import com.epages.datasource.DataSourceLookup;
import com.epages.metadata.JdbcCursorItemReader4EPJ;
import com.epages.product.Product;
import com.epages.product.attribute.entry.CustomAttribute;
import com.epages.product.attribute.entry.CustomAttributeValue;
import com.epages.product.attribute.entry.CustomSearchFilterAttributeType;
import com.epages.product.attribute.entry.LocalizedKey;
import com.epages.product.reader.AttributeValidator;
import com.epages.product.reader.BaseSingleItemPeekableReader;

/**
 * Example of using ProductTypes as facets.
 */
final class ProductTypeReader extends BaseSingleItemPeekableReader<CustomAttributeValue, CustomAttribute> {

    @Inject
    public ProductTypeReader(AttributeValidator validator, DataSourceLookup dataSourceLookup) {
        super(validator, new Reader(dataSourceLookup));
    }

    @Override
    protected void addToProduct(Product product, CustomAttribute attribute) {
        product.addAttribute(attribute);
    }

    static final class Reader extends JdbcCursorItemReader4EPJ<CustomAttribute> {

        private static final RowMapper<CustomAttribute> ROW_MAPPER =  new RowMapper<CustomAttribute>() {

            @Override
            public CustomAttribute mapRow(ResultSet rs, int rowNum) throws SQLException {
                final LocalizedKey key = new LocalizedKey("ProductType", new Locale(rs.getString("langcode")));
                CustomAttributeValue attributeValue = new CustomAttributeValue(
                        // TODO: You need to localize the term "Product Type" correctly.
                        ("de".equals(rs.getString("langcode")) ? "Produkttyp" : "Product type"),
                        CustomSearchFilterAttributeType.PreDefLocalizedString, new Locale(rs.getString("langcode")));
                attributeValue.setAttributeValue(rs.getString("attributevalue"), 0);
                attributeValue.setIsSearchfilter(1);
                return new CustomAttribute(rs.getInt("main_productid"), 0, key, attributeValue);
            }
        };

        public static final String SQL = new StringBuilder()
        .append("SELECT COALESCE(p.superproductid, p.productid) as main_productid,")
        .append(" l.code2 as langcode, lsa.value as attributevalue, o.alias, p.superproductid, p.productid ")
        .append(" FROM product p ")
        .append(" JOIN object o on p.productid=o.objectid")
        .append(" JOIN object oc on o.classid=oc.objectid")
         .append(" JOIN localizedstringattribute lsa on (o.classid = lsa.objectid) ")
         .append(" JOIN attribute a ON lsa.attributeid = a.attributeid AND a.type = 'LocalizedString' ")
         .append(" JOIN language  l ON l.languageid = lsa.languageid ")
         .append(" JOIN object lsao on lsa.attributeid=lsao.objectid")
         .append(" WHERE oc.alias != \"ProductClass\" AND lsao.alias = \"Name\"")
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
