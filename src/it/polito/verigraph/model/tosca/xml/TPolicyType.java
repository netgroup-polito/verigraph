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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per tPolicyType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="tPolicyType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/tosca/ns/2011/12}tEntityType">
 *       &lt;sequence>
 *         &lt;element name="AppliesTo" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tAppliesTo" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="policyLanguage" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tPolicyType", propOrder = {
    "appliesTo"
})
public class TPolicyType
    extends TEntityType
{

    @XmlElement(name = "AppliesTo")
    protected TAppliesTo appliesTo;
    @XmlAttribute(name = "policyLanguage")
    @XmlSchemaType(name = "anyURI")
    protected String policyLanguage;

    /**
     * Recupera il valore della proprietà appliesTo.
     * 
     * @return
     *     possible object is
     *     {@link TAppliesTo }
     *     
     */
    public TAppliesTo getAppliesTo() {
        return appliesTo;
    }

    /**
     * Imposta il valore della proprietà appliesTo.
     * 
     * @param value
     *     allowed object is
     *     {@link TAppliesTo }
     *     
     */
    public void setAppliesTo(TAppliesTo value) {
        this.appliesTo = value;
    }

    /**
     * Recupera il valore della proprietà policyLanguage.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPolicyLanguage() {
        return policyLanguage;
    }

    /**
     * Imposta il valore della proprietà policyLanguage.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPolicyLanguage(String value) {
        this.policyLanguage = value;
    }

}
