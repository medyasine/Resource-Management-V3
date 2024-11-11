package org.example.ecom.Converter;

import org.example.ecom.Service.CategoryDaoImp;
import org.example.ecom.model.Category;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "categoryConverter")
public class CategoryConverter implements Converter {
    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        System.out.println("CategoryConverter.getAsObject: " + value);
        System.out.println(new CategoryDaoImp().getById(Long.valueOf(value)));
        return new CategoryDaoImp().getById(Long.valueOf(value)).get();
    }

    @Override
    public String getAsString(javax.faces.context.FacesContext facesContext, javax.faces.component.UIComponent uiComponent, Object object) {
        if (!(object instanceof Category)) {
            return null;
        }
        System.out.println("CategoryConverter.getAsString: " + ((Category) object).getId());
        return String.valueOf(((Category) object).getId());
    }

}
