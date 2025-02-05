<?xml version='1.0' encoding='UTF-8' ?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<HTML
        xmlns:h="http://xmlns.jcp.org/jsf/html"
        xmlns:p="http://primefaces.org/ui"
>
<ui:compositiontemplate="./index.xhtml"
xmlns="http://www.w3.org/1999/xhtml"
xmlns:h="http://java.sun.com/jsf/html"
xmlns:ui="http://java.sun.com/jsf/facelets"
xmlns:p="http://primefaces.org/ui">

<ui:define name="title">Statistics</ui:define>
<ui:define name="content">

    <h:form>
        <h2>Product Statistics</h2>

        <!-- Load Statistics -->
        <p:commandButton value="Load Statistics" action="#{adminStatistics.loadStatistics}" />

        <h:panelGrid columns="2" cellpadding="10" styleClass="chart-container">
            <!-- Display the sales pie chart -->
            <p:chart type="pie" model="#{adminStatistics.salesChart}" id="salesChart" style="width: 100%; height: 400px;" />

            <h2>Category Distribution</h2>

            <!-- Category Bar Chart -->
            <p:chart type="bar" model="#{adminStatistics.categoryChart}" id="categoryChart" style="width: 100%; height: 400px;" />

        </h:panelGrid>
    </h:form>

</ui:define>
</ui:composition>
</HTML>