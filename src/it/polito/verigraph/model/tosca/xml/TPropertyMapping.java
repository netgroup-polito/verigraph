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
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per tPropertyMapping complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="tPropertyMapping">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="serviceTemplatePropertyRef" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="targetObjectRef" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *       &lt;attribute name="targetPropertyRef" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tPropertyMapping")
public class TPropertyMapping {

    @XmlAttribute(name = "serviceTemplatePropertyRef", required = true)
    protected String serviceTemplatePropertyRef;
    @XmlAttribute(name = "targetObjectRef", required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object targetObjectRef;
    @XmlAttribute(name = "targetPropertyRef", required = true)
    protected String targetPropertyRef;

    /**
     * Recupera il valore della proprietà serviceTemplatePropertyRef.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceTemplatePropertyRef() {
        return serviceTemplatePropertyRef;
    }

    /**
     * Imposta il valore della proprietà serviceTemplatePropertyRef.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceTemplatePropertyRef(String value) {
        this.serviceTemplatePropertyRef = value;
    }

    /**
     * Recupera il valore della proprietà targetObjectRef.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getTargetObjectRef() {
        return targetObjectRef;
    }

    /**
     * Imposta il valore della proprietà targetObjectRef.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setTargetObjectRef(Object value) {
        this.targetObjectRef = value;
    }

    /**
     * Recupera il valore della proprietà targetPropertyRef.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetPropertyRef() {
        return targetPropertyRef;
    }

    /**
     * Imposta il valore della proprietà targetPropertyRef.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetPropertyRef(String value) {
        this.targetPropertyRef = value;
    }

}
