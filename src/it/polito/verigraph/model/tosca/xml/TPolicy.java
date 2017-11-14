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
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * <p>Classe Java per tPolicy complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="tPolicy">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/tosca/ns/2011/12}tExtensibleElements">
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="policyType" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="policyRef" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tPolicy")
public class TPolicy
    extends TExtensibleElements
{

    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "policyType", required = true)
    protected QName policyType;
    @XmlAttribute(name = "policyRef")
    protected QName policyRef;

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
     * Recupera il valore della proprietà policyType.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getPolicyType() {
        return policyType;
    }

    /**
     * Imposta il valore della proprietà policyType.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setPolicyType(QName value) {
        this.policyType = value;
    }

    /**
     * Recupera il valore della proprietà policyRef.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getPolicyRef() {
        return policyRef;
    }

    /**
     * Imposta il valore della proprietà policyRef.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setPolicyRef(QName value) {
        this.policyRef = value;
    }

}
