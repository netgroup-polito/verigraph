//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2017.11.14 alle 10:51:41 AM CET 
//


package it.polito.verigraph.model.tosca.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * <p>Classe Java per tServiceTemplate complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="tServiceTemplate">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/tosca/ns/2011/12}tExtensibleElements">
 *       &lt;sequence>
 *         &lt;element name="Tags" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tTags" minOccurs="0"/>
 *         &lt;element name="BoundaryDefinitions" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tBoundaryDefinitions" minOccurs="0"/>
 *         &lt;element name="TopologyTemplate" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tTopologyTemplate"/>
 *         &lt;element name="Plans" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tPlans" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="targetNamespace" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="substitutableNodeType" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tServiceTemplate", propOrder = {
    "tags",
    "boundaryDefinitions",
    "topologyTemplate",
    "plans"
})
public class TServiceTemplate
    extends TExtensibleElements
{

    @XmlElement(name = "Tags")
    protected TTags tags;
    @XmlElement(name = "BoundaryDefinitions")
    protected TBoundaryDefinitions boundaryDefinitions;
    @XmlElement(name = "TopologyTemplate", required = true)
    protected TTopologyTemplate topologyTemplate;
    @XmlElement(name = "Plans")
    protected TPlans plans;
    @XmlAttribute(name = "id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "targetNamespace")
    @XmlSchemaType(name = "anyURI")
    protected String targetNamespace;
    @XmlAttribute(name = "substitutableNodeType")
    protected QName substitutableNodeType;

    /**
     * Recupera il valore della proprietà tags.
     * 
     * @return
     *     possible object is
     *     {@link TTags }
     *     
     */
    public TTags getTags() {
        return tags;
    }

    /**
     * Imposta il valore della proprietà tags.
     * 
     * @param value
     *     allowed object is
     *     {@link TTags }
     *     
     */
    public void setTags(TTags value) {
        this.tags = value;
    }

    /**
     * Recupera il valore della proprietà boundaryDefinitions.
     * 
     * @return
     *     possible object is
     *     {@link TBoundaryDefinitions }
     *     
     */
    public TBoundaryDefinitions getBoundaryDefinitions() {
        return boundaryDefinitions;
    }

    /**
     * Imposta il valore della proprietà boundaryDefinitions.
     * 
     * @param value
     *     allowed object is
     *     {@link TBoundaryDefinitions }
     *     
     */
    public void setBoundaryDefinitions(TBoundaryDefinitions value) {
        this.boundaryDefinitions = value;
    }

    /**
     * Recupera il valore della proprietà topologyTemplate.
     * 
     * @return
     *     possible object is
     *     {@link TTopologyTemplate }
     *     
     */
    public TTopologyTemplate getTopologyTemplate() {
        return topologyTemplate;
    }

    /**
     * Imposta il valore della proprietà topologyTemplate.
     * 
     * @param value
     *     allowed object is
     *     {@link TTopologyTemplate }
     *     
     */
    public void setTopologyTemplate(TTopologyTemplate value) {
        this.topologyTemplate = value;
    }

    /**
     * Recupera il valore della proprietà plans.
     * 
     * @return
     *     possible object is
     *     {@link TPlans }
     *     
     */
    public TPlans getPlans() {
        return plans;
    }

    /**
     * Imposta il valore della proprietà plans.
     * 
     * @param value
     *     allowed object is
     *     {@link TPlans }
     *     
     */
    public void setPlans(TPlans value) {
        this.plans = value;
    }

    /**
     * Recupera il valore della proprietà id.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Imposta il valore della proprietà id.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Recupera il valore della proprietà name.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Imposta il valore della proprietà name.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Recupera il valore della proprietà targetNamespace.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetNamespace() {
        return targetNamespace;
    }

    /**
     * Imposta il valore della proprietà targetNamespace.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetNamespace(String value) {
        this.targetNamespace = value;
    }

    /**
     * Recupera il valore della proprietà substitutableNodeType.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getSubstitutableNodeType() {
        return substitutableNodeType;
    }

    /**
     * Imposta il valore della proprietà substitutableNodeType.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setSubstitutableNodeType(QName value) {
        this.substitutableNodeType = value;
    }

}
