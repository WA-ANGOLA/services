package org.sola.admin.services.ejb.refdata.entities;

import javax.persistence.Table;
import org.sola.services.common.repository.DefaultSorter;
import org.sola.services.common.repository.entities.AbstractCodeEntity;

/**
 * Entity representing the application.negotiate_status code table.
 */
@Table(name = "land_project", schema = "opentenure")
@DefaultSorter(sortString="display_value")
public class LandProject extends AbstractCodeEntity {
    public LandProject() {
        super();
    }
}
