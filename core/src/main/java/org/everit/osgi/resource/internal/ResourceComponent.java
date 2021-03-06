/**
 * This file is part of Everit - Resource.
 *
 * Everit - Resource is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Everit - Resource is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Everit - Resource.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.everit.osgi.resource.internal;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.everit.osgi.resource.api.ExpandedSQLTemplates;
import org.everit.osgi.resource.api.ResourceService;
import org.everit.osgi.resource.schema.QResource;

import com.mysema.query.sql.SQLTemplates;
import com.mysema.query.sql.dml.SQLDeleteClause;
import com.mysema.query.sql.dml.SQLInsertClause;

/**
 * Implementation for {@link ResourceService}.
 */
@Component(name = "org.everit.osgi.resource.ResourceComponent", metatype = true, configurationFactory = true,
        policy = ConfigurationPolicy.REQUIRE)
@Properties({ @Property(name = "dataSource.target") })
@Service
public class ResourceComponent implements ResourceService {
    /**
     * {@link ExpandedSQLTemplates} instance for queries.
     */
    public static final SQLTemplates SQL_TEMPLATES = new ExpandedSQLTemplates();
    /**
     * {@link DataSource}.
     */
    @Reference
    private DataSource dataSource;

    public void bindDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public long createResource() {
        Long id = null;
        try (Connection connection = dataSource.getConnection()) {
            QResource qResource = new QResource("qResource");
            SQLInsertClause insertClause = new SQLInsertClause(connection, SQL_TEMPLATES, qResource);
            id = insertClause.executeWithKey(qResource.id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    @Override
    public long deleteResource(final long resourceId) {
        long deletedRecords = 0;
        try (Connection connection = dataSource.getConnection()) {
            QResource qResource = new QResource("qResource");
            SQLDeleteClause deleteClause = new SQLDeleteClause(connection, SQL_TEMPLATES, qResource);
            deletedRecords = deleteClause.where(qResource.id.eq(resourceId)).execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deletedRecords;
    }
}
