//
// Questo file Ŕ stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrÓ persa durante la ricompilazione dello schema di origine. 
// Generato il: 2017.11.14 alle 10:51:41 AM CET 
//


package it.polito.verigraph.model.tosca.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per tBoundaryDefinitions complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="tBoundaryDefinitions">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Properties" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;any namespace='##other'/>
 *                   &lt;element name="PropertyMappings" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="PropertyMapping" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tPropertyMapping" maxOccurs="unbounded"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="PropertyConstraints" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="PropertyConstraint" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tPropertyConstraint" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Requirements" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Requirement" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tRequirementRef" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Capabilities" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Capability" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tCapabilityRef" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Policies" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Policy" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tPolicy" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Interfaces" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Interface" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tExportedInterface" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tBoundaryDefinitions", propOrder = {
    "properties",
    "propertyConstraints",
    "requirements",
    "capabilities",
    "policies",
    "interfaces"
})
public class TBoundaryDefinitions {

    @XmlElement(name = "Properties")
    protected TBoundaryDefinitions.Properties properties;
    @XmlElement(name = "PropertyConstraints")
    protected TBoundaryDefinitions.PropertyConstraints propertyConstraints;
    @XmlElement(name = "Requirements")
    protected TBoundaryDefinitions.Requirements requirements;
    @XmlElement(name = "Capabilities")
    protected TBoundaryDefinitions.Capabilities capabilities;
    @XmlElement(name = "Policies")
    protected TBoundaryDefinitions.Policies policies;
    @XmlElement(name = "Interfaces")
    protected TBoundaryDefinitions.Interfaces interfaces;

    /**
     * Recupera il valore della proprietÓ properties.
     * 
     * @return
     *     possible object is
     *     {@link TBoundaryDefinitions.Properties }
     *     
     */
    public TBoundaryDefinitions.Properties getProperties() {
        return properties;
    }

    /**
     * Imposta il valore della proprietÓ properties.
     * 
     * @param value
     *     allowed object is
     *     {@link TBoundaryDefinitions.Properties }
     *     
     */
    public void setProperties(TBoundaryDefinitions.Properties value) {
        this.properties = value;
    }

    /**
     * Recupera il valore della proprietÓ propertyConstraints.
     * 
     * @return
     *     possible object is
     *     {@link TBoundaryDefinitions.PropertyConstraints }
     *     
     */
    public TBoundaryDefinitions.PropertyConstraints getPropertyConstraints() {
        return propertyConstraints;
    }

    /**
     * Imposta il valore della proprietÓ propertyConstraints.
     * 
     * @param value
     *     allowed object is
     *     {@link TBoundaryDefinitions.PropertyConstraints }
     *     
     */
    public void setPropertyConstraints(TBoundaryDefinitions.PropertyConstraints value) {
        this.propertyConstraints = value;
    }

    /**
     * Recupera il valore della proprietÓ requirements.
     * 
     * @return
     *     possible object is
     *     {@link TBoundaryDefinitions.Requirements }
     *     
     */
    public TBoundaryDefinitions.Requirements getRequirements() {
        return requirements;
    }

    /**
     * Imposta il valore della proprietÓ requirements.
     * 
     * @param value
     *     allowed object is
     *     {@link TBoundaryDefinitions.Requirements }
     *     
     */
    public void setRequirements(TBoundaryDefinitions.Requirements value) {
        this.requirements = value;
    }

    /**
     * Recupera il valore della proprietÓ capabilities.
     * 
     * @return
     *     possible object is
     *     {@link TBoundaryDefinitions.Capabilities }
     *     
     */
    public TBoundaryDefinitions.Capabilities getCapabilities() {
        return capabilities;
    }

    /**
     * Imposta il valore della proprietÓ capabilities.
     * 
     * @param value
     *     allowed object is
     *     {@link TBoundaryDefinitions.Capabilities }
     *     
     */
    public void setCapabilities(TBoundaryDefinitions.Capabilities value) {
        this.capabilities = value;
    }

    /**
     * Recupera il valore della proprietÓ policies.
     * 
     * @return
     *     possible object is
     *     {@link TBoundaryDefinitions.Policies }
     *     
     */
    public TBoundaryDefinitions.Policies getPolicies() {
        return policies;
    }

    /**
     * Imposta il valore della proprietÓ policies.
     * 
     * @param value
     *     allowed object is
     *     {@link TBoundaryDefinitions.Policies }
     *     
     */
    public void setPolicies(TBoundaryDefinitions.Policies value) {
        this.policies = value;
    }

    /**
     * Recupera il valore della proprietÓ interfaces.
     * 
     * @return
     *     possible object is
     *     {@link TBoundaryDefinitions.Interfaces }
     *     
     */
    public TBoundaryDefinitions.Interfaces getInterfaces() {
        return interfaces;
    }

    /**
     * Imposta il valore della proprietÓ interfaces.
     * 
     * @param value
     *     allowed object is
     *     {@link TBoundaryDefinitions.Interfaces }
     *     
     */
    public void setInterfaces(TBoundaryDefinitions.Interfaces value) {
        this.interfaces = value;
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
     *         &lt;element name="Capability" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tCapabilityRef" maxOccurs="unbounded"/>
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
        "capability"
    })
    public static class Capabilities {

        @XmlElement(name = "Capability", required = true)
        protected List<TCapabilityRef> capability;

        /**
         * Gets the value of the capability property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the capability property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getCapability().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TCapabilityRef }
         * 
         * 
         */
        public List<TCapabilityRef> getCapability() {
            if (capability == null) {
                capability = new ArrayList<TCapabilityRef>();
            }
            return this.capability;
        }

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
     *         &lt;element name="Interface" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tExportedInterface" maxOccurs="unbounded"/>
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
        "_interface"
    })
    public static class Interfaces {

        @XmlElement(name = "Interface", required = true)
        protected List<TExportedInterface> _interface;

        /**
         * Gets the value of the interface property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the interface property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getInterface().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TExportedInterface }
         * 
         * 
         */
        public List<TExportedInterface> getInterface() {
            if (_interface == null) {
                _interface = new ArrayList<TExportedInterface>();
            }
            return this._interface;
        }

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
     *         &lt;element name="Policy" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tPolicy" maxOccurs="unbounded"/>
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
        "policy"
    })
    public static class Policies {

        @XmlElement(name = "Policy", required = true)
        protected List<TPolicy> policy;

        /**
         * Gets the value of the policy property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the policy property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPolicy().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TPolicy }
         * 
         * 
         */
        public List<TPolicy> getPolicy() {
            if (policy == null) {
                policy = new ArrayList<TPolicy>();
            }
            return this.policy;
        }

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
     *         &lt;any namespace='##other'/>
     *         &lt;element name="PropertyMappings" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="PropertyMapping" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tPropertyMapping" maxOccurs="unbounded"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
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
        "any",
        "propertyMappings"
    })
    public static class Properties {

        @XmlAnyElement(lax = true)
        protected Object any;
        @XmlElement(name = "PropertyMappings")
        protected TBoundaryDefinitions.Properties.PropertyMappings propertyMappings;

        /**
         * Recupera il valore della proprietÓ any.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getAny() {
            return any;
        }

        /**
         * Imposta il valore della proprietÓ any.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setAny(Object value) {
            this.any = value;
        }

        /**
         * Recupera il valore della proprietÓ propertyMappings.
         * 
         * @return
         *     possible object is
         *     {@link TBoundaryDefinitions.Properties.PropertyMappings }
         *     
         */
        public TBoundaryDefinitions.Properties.PropertyMappings getPropertyMappings() {
            return propertyMappings;
        }

        /**
         * Imposta il valore della proprietÓ propertyMappings.
         * 
         * @param value
         *     allowed object is
         *     {@link TBoundaryDefinitions.Properties.PropertyMappings }
         *     
         */
        public void setPropertyMappings(TBoundaryDefinitions.Properties.PropertyMappings value) {
            this.propertyMappings = value;
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
         *         &lt;element name="PropertyMapping" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tPropertyMapping" maxOccurs="unbounded"/>
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
            "propertyMapping"
        })
        public static class PropertyMappings {

            @XmlElement(name = "PropertyMapping", required = true)
            protected List<TPropertyMapping> propertyMapping;

            /**
             * Gets the value of the propertyMapping property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the propertyMapping property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getPropertyMapping().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link TPropertyMapping }
             * 
             * 
             */
            public List<TPropertyMapping> getPropertyMapping() {
                if (propertyMapping == null) {
                    propertyMapping = new ArrayList<TPropertyMapping>();
                }
                return this.propertyMapping;
            }

        }

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
     *         &lt;element name="PropertyConstraint" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tPropertyConstraint" maxOccurs="unbounded"/>
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
        "propertyConstraint"
    })
    public static class PropertyConstraints {

        @XmlElement(name = "PropertyConstraint", required = true)
        protected List<TPropertyConstraint> propertyConstraint;

        /**
         * Gets the value of the propertyConstraint property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the propertyConstraint property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPropertyConstraint().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TPropertyConstraint }
         * 
         * 
         */
        public List<TPropertyConstraint> getPropertyConstraint() {
            if (propertyConstraint == null) {
                propertyConstraint = new ArrayList<TPropertyConstraint>();
            }
            return this.propertyConstraint;
        }

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
     *         &lt;element name="Requirement" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tRequirementRef" maxOccurs="unbounded"/>
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
        "requirement"
    })
    public static class Requirements {

        @XmlElement(name = "Requirement", required = true)
        protected List<TRequirementRef> requirement;

        /**
         * Gets the value of the requirement property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the requirement property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getRequirement().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TRequirementRef }
         * 
         * 
         */
        public List<TRequirementRef> getRequirement() {
            if (requirement == null) {
                requirement = new ArrayList<TRequirementRef>();
            }
            return this.requirement;
        }

    }

}
