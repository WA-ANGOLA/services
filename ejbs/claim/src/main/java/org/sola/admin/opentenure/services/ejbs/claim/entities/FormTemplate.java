package org.sola.admin.opentenure.services.ejbs.claim.entities;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import org.sola.services.common.repository.ChildEntityList;
import org.sola.services.common.repository.DefaultSorter;
import org.sola.services.common.repository.Localized;
import org.sola.services.common.repository.entities.AbstractVersionedEntity;

@Table(schema = "opentenure", name = "form_template")
@DefaultSorter(sortString = "name desc")
public class FormTemplate extends AbstractVersionedEntity {
    
    public static final String QUERY_GENERATE_FORM_NAME = "select generate_form_name as name from opentenure.generate_form_name()";
    
    @Id
    @Column
    private String name;
    @Column(name="display_name")
    @Localized
    private String displayName;
    @Column(name="is_default")
    private boolean isDefault;
    @ChildEntityList(parentIdField = "formTemplateName", cascadeDelete = true)
    private List<SectionTemplate> sectionTemplateList;
    
    public FormTemplate(){
    }
    
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public List<SectionTemplate> getSectionTemplateList() {
        return sectionTemplateList;
    }

    public void setSectionTemplateList(List<SectionTemplate> sectionTemplateList) {
        this.sectionTemplateList = sectionTemplateList;
    }
}
