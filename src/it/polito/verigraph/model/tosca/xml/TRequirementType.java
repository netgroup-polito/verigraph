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
 * <p>Classe Java per tRequirementType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="tRequirementType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/tosca/ns/2011/12}tEntityType">
 *       &lt;attribute name="requiredCapabilityType" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tRequirementType")
public class TRequirementType
    extends TEntityType
{

    @XmlAttribute(name = "requiredCapabilityType")
    protected QName requiredCapabilityType;

    /**
     * Recupera il valore della proprietà requiredCapabilityType.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getRequiredCapabilityType() {
        return requiredCapabilityType;
    }

    /**
     * Imposta il valore della proprietà requiredCapabilityType.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setRequiredCapabilityType(QName value) {
        this.requiredCapabilityType = value;
    }

}
