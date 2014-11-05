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
 * Example to add generic localized string attributes to the facets.
 * In order to make this example work, you need to set the "IsSearchFilter"
 * flag to the custom string attribute. This is not possible via MBO, as
 * we officially only support using this feature together with PredefAttributes.
 */
final class LocalizedCustomStringAttributeFacetsReader extends BaseSingleItemPeekableReader<CustomAttributeValue, CustomAttribute> {

    @Inject
    public LocalizedCustomStringAttributeFacetsReader(AttributeValidator validator, DataSourceLookup dataSourceLookup) {
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
                final LocalizedKey key = new LocalizedKey(rs.getString("attributekey"), new Locale(rs.getString("langcode")));
                CustomAttributeValue attributeValue = new CustomAttributeValue(rs.getString("attributename"),
                        CustomSearchFilterAttributeType.PreDefLocalizedString, new Locale(rs.getString("langcode")));
                attributeValue.setAttributeValue(rs.getString("attributevalue"), rs.getInt("attributeposition"));
                attributeValue.setIsSearchfilter(1);
                return new CustomAttribute(rs.getInt("main_productid"), getVariationId(rs), key, attributeValue);
            }
        };

        private static final String SQL = new StringBuilder()
            .append(" SELECT COALESCE(p.superproductid, p.productid) as main_productid, lsao.alias as attributekey,")
            .append(" COALESCE(name.value, lsao.alias) as attributename, lsao.position as attributeposition,")
            .append(" l.code2 as langcode, lsa.value as attributevalue, p.superproductid, p.productid ")
            .append(" FROM product p ")
            .append(" JOIN localizedstringattribute lsa on (p.productid = lsa.objectid) ")
            .append(" JOIN attribute a ON lsa.attributeid = a.attributeid AND a.type = 'LocalizedString' AND a.isuserdefined=1 AND a.isvisible=1 ")
            .append(" JOIN language  l ON l.languageid = lsa.languageid ")
            .append(" JOIN object lsao on lsa.attributeid=lsao.objectid ")
            .append(" JOIN facetedsearchattribute fsa ON a.attributeid = fsa.attributeid AND fsa.issearchfilter = 1")
            .append(" LEFT JOIN (localizedstringattribute name JOIN object o ON name.attributeid = o.objectid AND o.alias = 'Name') ")
            .append("   ON a.attributeid=name.objectid AND name.languageid = lsa.languageid ")
            .append(" WHERE ")
            .append(" p.shopid = ? %s ")
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
