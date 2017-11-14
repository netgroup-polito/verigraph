//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2017.11.14 alle 10:51:41 AM CET 
//


package it.polito.verigraph.model.tosca.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * <p>Classe Java per tCapabilityDefinition complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="tCapabilityDefinition">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/tosca/ns/2011/12}tExtensibleElements">
 *       &lt;sequence>
 *         &lt;element name="Constraints" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Constraint" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tConstraint" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="capabilityType" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="lowerBound" type="{http://www.w3.org/2001/XMLSchema}int" default="1" />
 *       &lt;attribute name="upperBound" default="1">
 *         &lt;simpleType>
 *           &lt;union>
 *             &lt;simpleType>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
 *                 &lt;pattern value="([1-9]+[0-9]*)"/>
 *               &lt;/restriction>
 *             &lt;/simpleType>
 *             &lt;simpleType>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                 &lt;enumeration value="unbounded"/>
 *               &lt;/restriction>
 *             &lt;/simpleType>
 *           &lt;/union>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tCapabilityDefinition", propOrder = {
    "constraints"
})
public class TCapabilityDefinition
    extends TExtensibleElements
{

    @XmlElement(name = "Constraints")
    protected TCapabilityDefinition.Constraints constraints;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "capabilityType", required = true)
    protected QName capabilityType;
    @XmlAttribute(name = "lowerBound")
    protected Integer lowerBound;
    @XmlAttribute(name = "upperBound")
    protected String upperBound;

    /**
     * Recupera il valore della proprietà constraints.
     * 
     * @return
     *     possible object is
     *     {@link TCapabilityDefinition.Constraints }
     *     
     */
    public TCapabilityDefinition.Constraints getConstraints() {
        return constraints;
    }

    /**
     * Imposta il valore della proprietà constraints.
     * 
     * @param value
     *     allowed object is
     *     {@link TCapabilityDefinition.Constraints }
     *     
     */
    public void setConstraints(TCapabilityDefinition.Constraints value) {
        this.constraints = value;
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
     * Recupera il valore della proprietà capabilityType.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getCapabilityType() {
        return capabilityType;
    }

    /**
     * Imposta il valore della proprietà capabilityType.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setCapabilityType(QName value) {
        this.capabilityType = value;
    }

    /**
     * Recupera il valore della proprietà lowerBound.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getLowerBound() {
        if (lowerBound == null) {
            return  1;
        } else {
            return lowerBound;
        }
    }

    /**
     * Imposta il valore della proprietà lowerBound.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLowerBound(Integer value) {
        this.lowerBound = value;
    }

    /**
     * Recupera il valore della proprietà upperBound.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUpperBound() {
        if (upperBound == null) {
            return "1";
        } else {
            return upperBound;
        }
    }

    /**
     * Imposta il valore della proprietà upperBound.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUpperBound(String value) {
        this.upperBound = value;
    }


    /**
     * <p>Classe Java per anonymous complex type.
     * 
     * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="Constraint" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tConstraint" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "constraint"
    })
    public static class Constraints {

        @XmlElement(name = "Constraint", required = true)
        protected List<TConstraint> constraint;

        /**
         * Gets the value of the constraint property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the constraint property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getConstraint().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TConstraint }
         * 
         * 
         */
        public List<TConstraint> getConstraint() {
            if (constraint == null) {
                constraint = new ArrayList<TConstraint>();
            }
            return this.constraint;
        }

    }

}
