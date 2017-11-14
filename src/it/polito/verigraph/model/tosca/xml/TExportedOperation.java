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
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java per tExportedOperation complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="tExportedOperation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="NodeOperation">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="nodeRef" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *                 &lt;attribute name="interfaceName" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *                 &lt;attribute name="operationName" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="RelationshipOperation">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="relationshipRef" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *                 &lt;attribute name="interfaceName" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *                 &lt;attribute name="operationName" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Plan">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="planRef" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/choice>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tExportedOperation", propOrder = {
    "nodeOperation",
    "relationshipOperation",
    "plan"
})
public class TExportedOperation {

    @XmlElement(name = "NodeOperation")
    protected TExportedOperation.NodeOperation nodeOperation;
    @XmlElement(name = "RelationshipOperation")
    protected TExportedOperation.RelationshipOperation relationshipOperation;
    @XmlElement(name = "Plan")
    protected TExportedOperation.Plan plan;
    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String name;

    /**
     * Recupera il valore della proprietà nodeOperation.
     * 
     * @return
     *     possible object is
     *     {@link TExportedOperation.NodeOperation }
     *     
     */
    public TExportedOperation.NodeOperation getNodeOperation() {
        return nodeOperation;
    }

    /**
     * Imposta il valore della proprietà nodeOperation.
     * 
     * @param value
     *     allowed object is
     *     {@link TExportedOperation.NodeOperation }
     *     
     */
    public void setNodeOperation(TExportedOperation.NodeOperation value) {
        this.nodeOperation = value;
    }

    /**
     * Recupera il valore della proprietà relationshipOperation.
     * 
     * @return
     *     possible object is
     *     {@link TExportedOperation.RelationshipOperation }
     *     
     */
    public TExportedOperation.RelationshipOperation getRelationshipOperation() {
        return relationshipOperation;
    }

    /**
     * Imposta il valore della proprietà relationshipOperation.
     * 
     * @param value
     *     allowed object is
     *     {@link TExportedOperation.RelationshipOperation }
     *     
     */
    public void setRelationshipOperation(TExportedOperation.RelationshipOperation value) {
        this.relationshipOperation = value;
    }

    /**
     * Recupera il valore della proprietà plan.
     * 
     * @return
     *     possible object is
     *     {@link TExportedOperation.Plan }
     *     
     */
    public TExportedOperation.Plan getPlan() {
        return plan;
    }

    /**
     * Imposta il valore della proprietà plan.
     * 
     * @param value
     *     allowed object is
     *     {@link TExportedOperation.Plan }
     *     
     */
    public void setPlan(TExportedOperation.Plan value) {
        this.plan = value;
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
     * <p>Classe Java per anonymous complex type.
     * 
     * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="nodeRef" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
     *       &lt;attribute name="interfaceName" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
     *       &lt;attribute name="operationName" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class NodeOperation {

        @XmlAttribute(name = "nodeRef", required = true)
        @XmlIDREF
        @XmlSchemaType(name = "IDREF")
        protected Object nodeRef;
        @XmlAttribute(name = "interfaceName", required = true)
        @XmlSchemaType(name = "anyURI")
        protected String interfaceName;
        @XmlAttribute(name = "operationName", required = true)
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlSchemaType(name = "NCName")
        protected String operationName;

        /**
         * Recupera il valore della proprietà nodeRef.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getNodeRef() {
            return nodeRef;
        }

        /**
         * Imposta il valore della proprietà nodeRef.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setNodeRef(Object value) {
            this.nodeRef = value;
        }

        /**
         * Recupera il valore della proprietà interfaceName.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getInterfaceName() {
            return interfaceName;
        }

        /**
         * Imposta il valore della proprietà interfaceName.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setInterfaceName(String value) {
            this.interfaceName = value;
        }

        /**
         * Recupera il valore della proprietà operationName.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getOperationName() {
            return operationName;
        }

        /**
         * Imposta il valore della proprietà operationName.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setOperationName(String value) {
            this.operationName = value;
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
     *       &lt;attribute name="planRef" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Plan {

        @XmlAttribute(name = "planRef", required = true)
        @XmlIDREF
        @XmlSchemaType(name = "IDREF")
        protected Object planRef;

        /**
         * Recupera il valore della proprietà planRef.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getPlanRef() {
            return planRef;
        }

        /**
         * Imposta il valore della proprietà planRef.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setPlanRef(Object value) {
            this.planRef = value;
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
     *       &lt;attribute name="relationshipRef" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
     *       &lt;attribute name="interfaceName" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
     *       &lt;attribute name="operationName" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class RelationshipOperation {

        @XmlAttribute(name = "relationshipRef", required = true)
        @XmlIDREF
        @XmlSchemaType(name = "IDREF")
        protected Object relationshipRef;
        @XmlAttribute(name = "interfaceName", required = true)
        @XmlSchemaType(name = "anyURI")
        protected String interfaceName;
        @XmlAttribute(name = "operationName", required = true)
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlSchemaType(name = "NCName")
        protected String operationName;

        /**
         * Recupera il valore della proprietà relationshipRef.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getRelationshipRef() {
            return relationshipRef;
        }

        /**
         * Imposta il valore della proprietà relationshipRef.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setRelationshipRef(Object value) {
            this.relationshipRef = value;
        }

        /**
         * Recupera il valore della proprietà interfaceName.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getInterfaceName() {
            return interfaceName;
        }

        /**
         * Imposta il valore della proprietà interfaceName.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setInterfaceName(String value) {
            this.interfaceName = value;
        }

        /**
         * Recupera il valore della proprietà operationName.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getOperationName() {
            return operationName;
        }

        /**
         * Imposta il valore della proprietà operationName.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setOperationName(String value) {
            this.operationName = value;
        }

    }

}
