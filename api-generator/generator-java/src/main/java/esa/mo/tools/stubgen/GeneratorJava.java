/* ----------------------------------------------------------------------------
 * Copyright (C) 2013      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : CCSDS MO Service Stub Generator
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
 * You may not use this file except in compliance with the License.
 *
 * Except as expressly set forth in this License, the Software is provided to
 * You on an "as is" basis and without warranties of any kind, including without
 * limitation merchantability, fitness for a particular purpose, absence of
 * defects or errors, accuracy or non-infringement of intellectual property rights.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 * ----------------------------------------------------------------------------
 */
package esa.mo.tools.stubgen;

import esa.mo.tools.stubgen.specification.AttributeTypeDetails;
import esa.mo.tools.stubgen.specification.CompositeField;
import esa.mo.tools.stubgen.specification.NativeTypeDetails;
import esa.mo.tools.stubgen.specification.OperationSummary;
import esa.mo.tools.stubgen.specification.ServiceSummary;
import esa.mo.tools.stubgen.specification.StdStrings;
import esa.mo.tools.stubgen.specification.TypeUtils;
import esa.mo.tools.stubgen.writers.ClassWriter;
import esa.mo.tools.stubgen.writers.InterfaceWriter;
import esa.mo.tools.stubgen.writers.LanguageWriter;
import esa.mo.tools.stubgen.writers.MethodWriter;
import esa.mo.tools.stubgen.writers.TargetWriter;
import esa.mo.xsd.AreaType;
import esa.mo.xsd.EnumerationType;
import esa.mo.xsd.ServiceType;
import esa.mo.xsd.TypeReference;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Generates stubs and skeletons for CCSDS MO Service specifications for the
 * Java language.
 */
public class GeneratorJava extends GeneratorLangs {

    /**
     * The file extension for Java files.
     */
    public static final String JAVA_FILE_EXT = "java";
    /**
     * The file name for package level comments.
     */
    public static final String JAVA_PACKAGE_COMMENT_FILE_NAME = "package-info";

    /**
     * Constructor.
     *
     * @param logger The logger to use.
     */
    public GeneratorJava(org.apache.maven.plugin.logging.Log logger) {
        super(logger, true, true, false, true, false,
                new GeneratorConfiguration("org.ccsds.moims.mo.",
                        "structures",
                        "factory",
                        "body",
                        ".",
                        "(Object[]) null",
                        "MALSendOperation",
                        "MALSubmitOperation",
                        "MALRequestOperation",
                        "MALInvokeOperation",
                        "MALProgressOperation",
                        "MALPubSubOperation"));
    }

    @Override
    public String getShortName() {
        return "Java";
    }

    @Override
    public String getDescription() {
        return "Generates a Java language mapping.";
    }

    @Override
    public void init(String destinationFolderName,
            boolean generateStructures,
            boolean generateCOM,
            Map<String, String> packageBindings,
            Map<String, String> extraProperties) throws IOException {
        super.init(destinationFolderName, generateStructures, generateCOM, packageBindings, extraProperties);

        setRequiresDefaultConstructors(Boolean.valueOf(extraProperties.get("java.requiresDefaultConstructors")));
        setSupportFullyPolymorphicTypes(Boolean.valueOf(extraProperties.get("java.supportFullyPolymorphicTypes")));

        addAttributeType(StdStrings.MAL, StdStrings.BLOB, false, "Blob", "");
        addAttributeType(StdStrings.MAL, StdStrings.BOOLEAN, true, "Boolean", "Boolean.FALSE");
        addAttributeType(StdStrings.MAL, StdStrings.DOUBLE, true, "Double", "Double.MAX_VALUE");
        addAttributeType(StdStrings.MAL, StdStrings.DURATION, false, "Duration", "");
        addAttributeType(StdStrings.MAL, StdStrings.FLOAT, true, "Float", "Float.MAX_VALUE");
        addAttributeType(StdStrings.MAL, StdStrings.INTEGER, true, "Integer", "Integer.MAX_VALUE");
        addAttributeType(StdStrings.MAL, StdStrings.IDENTIFIER, false, "Identifier", "");
        addAttributeType(StdStrings.MAL, StdStrings.LONG, true, "Long", "Long.MAX_VALUE");
        addAttributeType(StdStrings.MAL, StdStrings.OCTET, true, "Byte", "Byte.MAX_VALUE");
        addAttributeType(StdStrings.MAL, StdStrings.SHORT, true, "Short", "Short.MAX_VALUE");
        addAttributeType(StdStrings.MAL, StdStrings.UINTEGER, false, "UInteger", "");
        addAttributeType(StdStrings.MAL, StdStrings.ULONG, false, "ULong", "");
        addAttributeType(StdStrings.MAL, StdStrings.UOCTET, false, "UOctet", "");
        addAttributeType(StdStrings.MAL, StdStrings.USHORT, false, "UShort", "");
        addAttributeType(StdStrings.MAL, StdStrings.STRING, true, "String", "\"\"");
        addAttributeType(StdStrings.MAL, StdStrings.TIME, false, "Time", "");
        addAttributeType(StdStrings.MAL, StdStrings.FINETIME, false, "FineTime", "");
        addAttributeType(StdStrings.MAL, StdStrings.URI, false, "URI", "");
        addAttributeType(StdStrings.MAL, StdStrings.OBJECTREF, false, "ObjectRef", "");

        super.addNativeType("boolean", new NativeTypeDetails("boolean", false, false, null));
        super.addNativeType("_String", new NativeTypeDetails("String", false, false, null));
        super.addNativeType("byte", new NativeTypeDetails("byte", false, false, null));
        super.addNativeType("short", new NativeTypeDetails("short", false, false, null));
        super.addNativeType("int", new NativeTypeDetails("int", false, false, null));
        super.addNativeType("long", new NativeTypeDetails("long", false, false, null));
        super.addNativeType("_Integer", new NativeTypeDetails("Integer", true, false, null));
        super.addNativeType("Class", new NativeTypeDetails("Class", true, false, null));
        super.addNativeType("Map", new NativeTypeDetails("java.util.Map", true, false, null));
        super.addNativeType("Vector", new NativeTypeDetails("java.util.Vector", true, false, null));
    }

    @Override
    public void createRequiredPublisher(String destinationFolderName, String fqPublisherName, RequiredPublisher publisher) throws IOException {
        getLog().info("Creating publisher class " + fqPublisherName);

        String publisherName = fqPublisherName.substring(fqPublisherName.lastIndexOf('.') + 1);
        ClassWriter file = createClassFile(destinationFolderName, fqPublisherName.replace('.', '/'));

        file.addPackageStatement(publisher.area, publisher.service, PROVIDER_FOLDER);

        String throwsMALException = createElementType(file, StdStrings.MAL, null, null, StdStrings.MALEXCEPTION);
        String throwsInteractionException = createElementType(file, StdStrings.MAL, null, null, StdStrings.MALINTERACTIONEXCEPTION);
        String throwsInteractionAndMALException = throwsInteractionException + ", " + throwsMALException;
        String throwsExceptions = "java.lang.IllegalArgumentException, " + throwsInteractionAndMALException;
        CompositeField publisherSetType = createCompositeElementsDetails(file, false, "publisherSet",
                TypeUtils.createTypeReference(StdStrings.MAL, PROVIDER_FOLDER, "MALPublisherSet", false),
                false, true, null);

        file.addClassOpenStatement(publisherName, true, false, null, null,
                "Publisher class for the " + publisher.operation.getName() + " operation.");

        file.addClassVariable(false, false, StdStrings.PRIVATE, publisherSetType, false, (String) null);

        MethodWriter method = file.addConstructor(StdStrings.PUBLIC, publisherName,
                createCompositeElementsDetails(file, false, "publisherSet",
                        TypeUtils.createTypeReference(StdStrings.MAL, PROVIDER_FOLDER, "MALPublisherSet", false),
                        false, true, "publisherSet The set of broker connections to use when registering and publishing."),
                false, null, "Creates an instance of this class using the supplied publisher set.", null);
        method.addLine("this.publisherSet = publisherSet");
        method.addMethodCloseStatement();

        CompositeField keyList = createCompositeElementsDetails(file, false, "keys",
                TypeUtils.createTypeReference(StdStrings.MAL, null, "Identifier", true),
                true, true, "keys The keys to use in the method");
        CompositeField psListener = createCompositeElementsDetails(file, false, "listener",
                TypeUtils.createTypeReference(StdStrings.MAL, PROVIDER_FOLDER, "MALPublishInteractionListener", false),
                false, true,
                "listener The listener object to use for callback from the publisher");
        List<CompositeField> argPSListenerList = new LinkedList<>();
        argPSListenerList.add(keyList);
        argPSListenerList.add(psListener);
        method = file.addMethodOpenStatement(false, false, StdStrings.PUBLIC,
                false, true, null, "register", argPSListenerList, throwsExceptions,
                "Registers this provider implementation to the set of broker connections", null,
                Arrays.asList("java.lang.IllegalArgumentException If any supplied argument is invalid",
                        throwsInteractionException + " if there is a problem during the interaction as defined by the MAL specification.",
                        throwsMALException + " if there is an implementation exception"));
        method.addLine("publisherSet.register(keys, listener)");
        method.addMethodCloseStatement();

        method = file.addMethodOpenStatement(false, false, StdStrings.PUBLIC,
                false, true, null, "asyncRegister", argPSListenerList, throwsExceptions,
                "Asynchronously registers this provider implementation to the set of broker connections", null,
                Arrays.asList("java.lang.IllegalArgumentException If any supplied argument is invalid",
                        throwsInteractionException + " if there is a problem during the interaction as defined by the MAL specification.",
                        throwsMALException + " if there is an implementation exception"));
        method.addLine("publisherSet.asyncRegister(keys, listener)");
        method.addMethodCloseStatement();

        List<CompositeField> argList = new LinkedList<>();
        argList.add(createCompositeElementsDetails(file, true, "updateHeaderList",
                TypeUtils.createTypeReference(StdStrings.MAL, null, "UpdateHeader", true), true, true,
                "updateHeaderList The headers of the updates being added"));
        argList.addAll(createOperationArguments(getConfig(), file, publisher.operation.getUpdateTypes(), true));

        String argNameList = "";

        if (1 < argList.size()) {
            List<String> strList = new LinkedList<>();

            for (int i = 1; i < argList.size(); i++) {
                strList.add(argList.get(i).getFieldName());
            }

            argNameList = StubUtils.concatenateStringArguments(true, strList.toArray(new String[0]));
        }

        method = file.addMethodOpenStatement(false, false, StdStrings.PUBLIC,
                false, true, null, "publish", argList, throwsExceptions,
                "Publishes updates to the set of registered broker connections", null,
                Arrays.asList("java.lang.IllegalArgumentException If any supplied argument is invalid",
                        throwsInteractionException + " if there is a problem during the interaction as defined by the MAL specification.",
                        throwsMALException + " if there is an implementation exception"));
        method.addLine("publisherSet.publish(updateHeaderList" + argNameList + ")");
        method.addMethodCloseStatement();

        method = file.addMethodOpenStatement(false, false, StdStrings.PUBLIC,
                false, true, null, "deregister", null, throwsInteractionAndMALException,
                "Deregisters this provider implementation from the set of broker connections", null,
                Arrays.asList(throwsInteractionException + " if there is a problem during the interaction as defined by the MAL specification.",
                        throwsMALException + " if there is an implementation exception"));
        method.addLine("publisherSet.deregister()");
        method.addMethodCloseStatement();

        method = file.addMethodOpenStatement(false, false, StdStrings.PUBLIC,
                false, true, null, "asyncDeregister", Arrays.asList(psListener), throwsExceptions,
                "Asynchronously deregisters this provider implementation from the set of broker connections", null,
                Arrays.asList("java.lang.IllegalArgumentException If any supplied argument is invalid",
                        throwsInteractionException + " if there is a problem during the interaction as defined by the MAL specification.",
                        throwsMALException + " if there is an implementation exception"));
        method.addLine("publisherSet.asyncDeregister(listener)");
        method.addMethodCloseStatement();

        method = file.addMethodOpenStatement(false, false, StdStrings.PUBLIC,
                false, true, null, "close", null, throwsMALException,
                "Closes this publisher", null, Arrays.asList(throwsMALException + " if there is an implementation exception"));
        method.addLine("publisherSet.close()");
        method.addMethodCloseStatement();

        file.addClassCloseStatement();

        file.flush();
    }

    @Override
    protected void createListClass(File folder, AreaType area, ServiceType service,
            String srcTypeName, boolean isAbstract, Long shortFormPart) throws IOException {
        if (isAbstract) {
            createAbstractListClass(folder, area, service, srcTypeName);
        } else {
            createConcreteListClass(folder, area, service, srcTypeName, shortFormPart);
        }
    }

    /**
     * Creates a list for an abstract type.
     *
     * @param folder The base folder to create the list in.
     * @param area The Area of the list.
     * @param service The service of the list.
     * @param srcTypeName The name of the element in the list.
     * @throws IOException if there is a problem writing the file.
     */
    protected void createAbstractListClass(File folder, AreaType area,
            ServiceType service, String srcTypeName) throws IOException {
        String listName = srcTypeName + "List";

        getLog().info("Creating list interface " + listName);

        InterfaceWriter file = createInterfaceFile(folder, listName);

        file.addPackageStatement(area, service, getConfig().getStructureFolder());

        TypeReference typeRef = TypeUtils.createTypeReference(area.getName(), (null == service) ? null : service.getName(), srcTypeName, false);
        TypeReference superTypeReference = getCompositeElementSuperType(typeRef);
        String fqSrcTypeName = createElementType(file, area, service, srcTypeName);

        if (null == superTypeReference) {
            superTypeReference = new TypeReference();
            superTypeReference.setArea(StdStrings.MAL);
            String name = (isComposite(typeRef)) ? StdStrings.COMPOSITE : StdStrings.ELEMENT;
            superTypeReference.setName(name);
        }

        CompositeField listSuperElement = createCompositeElementsDetails(file, false, null,
                superTypeReference, true, true, "List element.");

        file.addInterfaceOpenStatement(listName + "<T extends " + fqSrcTypeName + ">",
                listSuperElement.getTypeName() + "List<T>",
                "List class for " + srcTypeName + "." + file.getLineSeparator() + " * @param <T> The type of this list must extend " + srcTypeName);
        file.addInterfaceCloseStatement();

        file.flush();
    }

    /**
     * Creates a list for an abstract type.
     *
     * @param folder The base folder to create the list in.
     * @param area The Area of the list.
     * @param service The service of the list.
     * @param srcTypeName The name of the element in the list.
     * @param shortFormPart The short form part of the contained element.
     * @throws IOException if there is a problem writing the file.
     */
    protected void createConcreteListClass(File folder, AreaType area, ServiceType service,
            String srcTypeName, Long shortFormPart) throws IOException {
        String listName = srcTypeName + "List";

        TypeReference srcType = new TypeReference();
        srcType.setArea(area.getName());
        if (null != service) {
            srcType.setService(service.getName());
        }

        srcType.setName(srcTypeName);
        getLog().info("Creating list class " + listName);
        ClassWriter file = createClassFile(folder, listName);

        file.addPackageStatement(area, service, getConfig().getStructureFolder());

        CompositeField elementType = createCompositeElementsDetails(file, false, "return",
                TypeUtils.createTypeReference(StdStrings.MAL, null, StdStrings.ELEMENT, false),
                true, true, null);
        String fqSrcTypeName = createElementType(file, area, service, srcTypeName);

        TypeReference superTypeReference = getCompositeElementSuperType(srcType);
        if (null == superTypeReference) {
            superTypeReference = new TypeReference();
            superTypeReference.setArea(StdStrings.MAL);
            if (isAttributeType(srcType)) {
                superTypeReference.setName(StdStrings.ATTRIBUTE);
            } else if (isEnum(srcType)) {
                superTypeReference.setName(StdStrings.ENUMERATION);
            } else {
                superTypeReference.setName(StdStrings.COMPOSITE);
            }
        }

        CompositeField listSuperElement = createCompositeElementsDetails(file, false, null,
                superTypeReference, true, true, "List element.");
        String sElement = listSuperElement.getTypeName();
        if (sElement.contains("Attribute")) {
            sElement = sElement.replace("Attribute", "Element"); // Needs to be replaced for Attributes
        }

        file.addClassOpenStatement(listName, true, false, "java.util.ArrayList<" + fqSrcTypeName + ">",
                sElement + "List<" + fqSrcTypeName + ">", "List class for " + srcTypeName + ".");

        CompositeField listElement = createCompositeElementsDetails(file, true, null,
                srcType, true, true, "List element.");

        addTypeShortFormDetails(file, area, service, -shortFormPart);

        // create blank constructor
        file.addConstructorDefault(listName);

        // create initial size contructor
        MethodWriter method = file.addConstructor(StdStrings.PUBLIC, listName,
                createCompositeElementsDetails(file, false, "initialCapacity",
                        TypeUtils.createTypeReference(null, null, "int", false),
                        false, false, "initialCapacity the required initial capacity."),
                true, null, "Constructor that initialises the capacity of the list.", null);
        method.addMethodCloseStatement();

        // create contructor with ArrayList 
        method = file.addConstructor(StdStrings.PUBLIC, listName,
                createCompositeElementsDetails(file, false, "elementList",
                        TypeUtils.createTypeReference(null, null, "java.util.ArrayList<" + fqSrcTypeName + ">", false),
                        false, false, "The ArrayList that is used for initialization."),
                false, null, "Constructor that uses an ArrayList for initialization.", null);
        method.addLine("for(" + fqSrcTypeName + " element : elementList) {", false);
        method.addLine("    super.add(element)");
        method.addLine("}", false);
        method.addMethodCloseStatement();

        List<CompositeField> argList = new LinkedList<>();
        argList.add(createCompositeElementsDetails(file, true, "element", srcType, true, true, "List element."));
        TypeReference type = new TypeReference();
        type.setName("boolean");
        CompositeField rtype = createCompositeElementsDetails(file, false, "element", type, false, true, "List element.");
        method = file.addMethodOpenStatement(true, false, StdStrings.PUBLIC,
                false, true, rtype, "add", argList, null,
                "Adds an element to the list and checks if it is not null.", "The success status.", null);
        /*
        method.addMethodStatement("if (element == null) {", false);
        method.addMethodStatement("  throw new IllegalArgumentException(\"The added argument cannot be null!\")");
        method.addMethodStatement("}", false);
         */
        method.addLine("return super.add(element)");
        method.addMethodCloseStatement();

        method = file.addMethodOpenStatement(true, false, StdStrings.PUBLIC, false,
                true, elementType, "createElement", null, null,
                "Creates an instance of this type using the default constructor. It is a generic factory method.",
                "A new instance of this type with default field values.", null);
        method.addLine("return new " + listName + "()");
        method.addMethodCloseStatement();

        // create encode method
        method = encodeMethodOpen(file);
        method.addLine("org.ccsds.moims.mo.mal.MALListEncoder listEncoder = encoder.createListEncoder(this)");
        method.addLine("for (int i = 0; i < size(); i++) {", false);
        method.addLine("  listEncoder.encodeNullable" + listElement.getEncodeCall() + "((" + fqSrcTypeName + ") get(i))");
        method.addLine("}", false);
        method.addLine("listEncoder.close()");
        method.addMethodCloseStatement();

        // create decode method
        method = decodeMethodOpen(file, elementType);
        method.addLine("org.ccsds.moims.mo.mal.MALListDecoder listDecoder = decoder.createListDecoder(this)");
        method.addLine("int decodedSize = listDecoder.size()");
        method.addLine("if (decodedSize > 0) {", false);
        method.addLine("  ensureCapacity(decodedSize)");
        method.addLine("}", false);
        method.addLine("while (listDecoder.hasNext()) {", false);
        method.addLine("  add(" + listElement.getDecodeCast() + "listDecoder.decodeNullable" + listElement.getDecodeCall()
                + "(" + (listElement.isDecodeNeedsNewCall() ? listElement.getNewCall() : "") + "))");
        method.addLine("}", false);
        method.addLine("return this");
        method.addMethodCloseStatement();

        addShortFormMethods(file);

        file.addClassCloseStatement();

        file.flush();

        srcType.setList(Boolean.TRUE);
        CompositeField listType = createCompositeElementsDetails(file, false, null,
                srcType, true, true, "List element.");
        createFactoryClass(folder, area, service, listName, listType, false, false);
    }

    @Override
    protected void addTypeShortForm(ClassWriter file, long sf) throws IOException {
        file.addMultilineComment("Short form for type.");
        file.addStatement("    public static final Integer TYPE_SHORT_FORM = " + sf + ";");
    }

    @Override
    protected void addShortForm(ClassWriter file, long sf) throws IOException {
        file.addMultilineComment("Absolute short form for type.");
        file.addStatement("    public static final Long SHORT_FORM = " + sf + "L;");
        file.addStatement("    private static final long serialVersionUID = " + sf + "L;");
    }

    @Override
    protected void createAreaFolderComment(File structureFolder, AreaType area) throws IOException {
        String cmt = area.getComment();
        if (null == cmt) {
            cmt = "The " + area.getName() + " area.";
        }

        createFolderComment(structureFolder, area, null, null, cmt);
    }

    @Override
    protected void createServiceFolderComment(File structureFolder,
            AreaType area, ServiceType service) throws IOException {
        createFolderComment(structureFolder, area, service, null, service.getComment());
    }

    @Override
    protected void createServiceConsumerFolderComment(File structureFolder,
            AreaType area, ServiceType service) throws IOException {
        createFolderComment(structureFolder, area, service, CONSUMER_FOLDER,
                "Package containing the consumer stubs for the " + service.getName() + " service.");
    }

    @Override
    protected void createServiceProviderFolderComment(File structureFolder,
            AreaType area, ServiceType service) throws IOException {
        createFolderComment(structureFolder, area, service, PROVIDER_FOLDER,
                "Package containing the provider skeletons for the " + service.getName() + " service.");
    }

    @Override
    protected void createServiceMessageBodyFolderComment(String baseFolder,
            AreaType area, ServiceType service) throws IOException {
        String basePackageName = getConfig().getAreaPackage(area.getName());
        String packageName = basePackageName + "." + area.getName().toLowerCase();
        if (null != service) {
            packageName += "." + service.getName().toLowerCase();
        }

        String className = packageName + "." + getConfig().getBodyFolder() + "." + JAVA_PACKAGE_COMMENT_FILE_NAME;
        className = className.replace('.', '/');
        ClassWriter file = createClassFile(baseFolder, className);

        createFolderComment(file, area, service, getConfig().getBodyFolder(),
                "Package containing the types for holding compound messages.");
    }

    @Override
    protected void createAreaStructureFolderComment(File structureFolder, AreaType area) throws IOException {
        createFolderComment(structureFolder, area, null, getConfig().getStructureFolder(),
                "Package containing types defined in the " + area.getName() + " area.");
    }

    @Override
    protected void createServiceStructureFolderComment(File structureFolder,
            AreaType area, ServiceType service) throws IOException {
        createFolderComment(structureFolder, area, service, getConfig().getStructureFolder(),
                "Package containing types defined in the " + service.getName() + " service.");
    }

    @Override
    protected void createStructureFactoryFolderComment(File structureFolder,
            AreaType area, ServiceType service) throws IOException {
        createFolderComment(structureFolder, area, service,
                getConfig().getStructureFolder() + "." + getConfig().getFactoryFolder(),
                "Factory classes for the types defined in the "
                + ((null == service) ? (area.getName() + " area.") : (service.getName() + " service.")));
    }

    /**
     * Creates a java package file.
     *
     * @param structureFolder The folder containing the generated code.
     * @param area the area.
     * @param service the server.
     * @param extraPackage any extra package level.
     * @param comment the comment.
     * @throws IOException if there is a problem.
     */
    protected void createFolderComment(File structureFolder, AreaType area,
            ServiceType service, String extraPackage, String comment) throws IOException {
        ClassWriter file = createClassFile(structureFolder, JAVA_PACKAGE_COMMENT_FILE_NAME);

        createFolderComment(file, area, service, extraPackage, comment);
    }

    /**
     * Creates a java package file.
     *
     * @param file The folder containing the generated code.
     * @param area the area.
     * @param service the server.
     * @param extraPackage any extra package level.
     * @param comment the comment.
     * @throws IOException if there is a problem.
     */
    protected void createFolderComment(ClassWriter file, AreaType area,
            ServiceType service, String extraPackage, String comment) throws IOException {
        file.addStatement("/**");
        file.addStatement(comment);
        file.addStatement("*/");
        file.addPackageStatement(area, service, extraPackage);

        file.flush();
    }

    @Override
    protected CompositeField createCompositeElementsDetails(TargetWriter file, boolean checkType,
            String fieldName, TypeReference elementType, boolean isStructure, boolean canBeNull, String comment) {
        CompositeField ele;

        String typeName = elementType.getName();

        if (checkType && !super.isKnownType(elementType)) {
            getLog().warn("Unknown type (" + new TypeKey(elementType)
                    + ") is being referenced as field (" + fieldName + ")");
        }

        if (elementType.isList()) {
//      if (StdStrings.XML.equals(elementType.getArea()))
//      {
//        throw new IllegalArgumentException("XML type of (" + elementType.getService() 
//                + ":" + elementType.getName() + ") with maxOccurrs <> 1 is not permitted");
//      }
//      else
            {
                String fqTypeName;
                if (isAttributeNativeType(elementType)) {
                    fqTypeName = createElementType((LanguageWriter) file, StdStrings.MAL, null, typeName + "List");
                } else {
                    fqTypeName = createElementType((LanguageWriter) file, elementType, true) + "List";
                }

                String newCall = null;
                String encCall = null;
                if (!isAbstract(elementType) || isFullyPolymorphic()) {
                    newCall = "new " + fqTypeName + "()";
                    encCall = StdStrings.ELEMENT;
                }

                ele = new CompositeField(fqTypeName, elementType, fieldName, elementType.isList(),
                        canBeNull, false, encCall, "(" + fqTypeName + ") ",
                        StdStrings.ELEMENT, true, newCall, comment);
            }
        } else if (isAttributeType(elementType)) {
            AttributeTypeDetails details = getAttributeDetails(elementType);
            String fqTypeName = createElementType((LanguageWriter) file, elementType, isStructure);
            ele = new CompositeField(details.getTargetType(), elementType, fieldName, elementType.isList(),
                    canBeNull, false, typeName, "", typeName, false, "new " + fqTypeName + "()", comment);
        } else {
            TypeReference elementTypeIndir = elementType;

            // have to work around the fact that JAXB does not replicate the XML type name into Java in all cases
            if (StdStrings.XML.equalsIgnoreCase(elementType.getArea())) {
                elementTypeIndir = TypeUtils.createTypeReference(elementType.getArea(),
                        elementType.getService(), StubUtils.preCap(elementType.getName()), elementType.isList());
            }

            String fqTypeName = createElementType((LanguageWriter) file, elementTypeIndir, isStructure);

            if (isEnum(elementType)) {
                EnumerationType typ = getEnum(elementType);
                String firstEle = fqTypeName + "." + typ.getItem().get(0).getValue();
                ele = new CompositeField(fqTypeName, elementType, fieldName, elementType.isList(),
                        canBeNull, false, StdStrings.ELEMENT, "(" + fqTypeName + ") ",
                        StdStrings.ELEMENT, true, firstEle, comment);
            } else if (StdStrings.ATTRIBUTE.equals(typeName)) {
                ele = new CompositeField(fqTypeName, elementType, fieldName, elementType.isList(),
                        canBeNull, false, StdStrings.ATTRIBUTE, "(" + fqTypeName + ") ",
                        StdStrings.ATTRIBUTE, false, "", comment);
            } else if (StdStrings.ELEMENT.equals(typeName)) {
                ele = new CompositeField(fqTypeName, elementType, fieldName, elementType.isList(),
                        canBeNull, false, StdStrings.ELEMENT, "(" + fqTypeName + ") ",
                        StdStrings.ELEMENT, false, "", comment);
            } else {
                ele = new CompositeField(fqTypeName, elementType, fieldName, elementType.isList(),
                        canBeNull, false, StdStrings.ELEMENT, "(" + fqTypeName + ") ",
                        StdStrings.ELEMENT, true, "new " + fqTypeName + "()", comment);
            }
        }

        return ele;
    }

    @Override
    protected String createAreaHelperClassInitialValue(String areaVar, short areaVersion) {
        return "(" + areaVar + "_AREA_NUMBER, " + areaVar + "_AREA_NAME, new org.ccsds.moims.mo.mal.structures.UOctet((short) " + areaVersion + "))";
    }

    @Override
    protected String getIntCallMethod() {
        return "intValue";
    }

    @Override
    protected String getOctetCallMethod() {
        return "byteValue";
    }

    @Override
    protected String getRegisterMethodName() {
        return "register";
    }

    @Override
    protected String getDeregisterMethodName() {
        return "deregister";
    }

    @Override
    protected String getEnumValueCompare(String lhs, String rhs) {
        return lhs + ".equals(" + rhs + ")";
    }

    @Override
    protected String getEnumEncoderValue(long maxValue) {
        String enumEncoderValue = "new org.ccsds.moims.mo.mal.structures.UInteger(ordinal.longValue())";
        if (maxValue < 256) {
            enumEncoderValue = "new org.ccsds.moims.mo.mal.structures.UOctet(ordinal.shortValue())";
        } else if (maxValue < 65536) {
            enumEncoderValue = "new org.ccsds.moims.mo.mal.structures.UShort(ordinal.intValue())";
        }

        return enumEncoderValue;
    }

    @Override
    protected String getEnumDecoderValue(long maxValue) {
        return ".getValue()";
    }

    @Override
    protected String getNullValue() {
        return "null";
    }

    @Override
    protected void addVectorAddStatement(LanguageWriter file, MethodWriter method,
            String variable, String parameter) throws IOException {
        method.addLine(variable + ".addElement(" + parameter + ")");
    }

    @Override
    protected void addVectorRemoveStatement(LanguageWriter file, MethodWriter method,
            String variable, String parameter) throws IOException {
        method.addLine(variable + ".removeElement(" + parameter + ")");
    }

    @Override
    protected String createStaticClassReference(String type) {
        return type + ".class";
    }

    @Override
    protected String addressOf(String type) {
        return type;
    }

    @Override
    protected String createArraySize(boolean isActual, String type, String variable) {
        return variable + ".length";
    }

    @Override
    protected String malStringAsElement(LanguageWriter file) {
        return createElementType(file, StdStrings.MAL, null, StdStrings.UNION);
    }

    @Override
    protected String errorCodeAsReference(LanguageWriter file, String ref) {
        return ref;
    }

    @Override
    protected ClassWriterProposed createClassFile(File folder, String className) throws IOException {
        return new JavaClassWriter(folder, className, this);
    }

    @Override
    protected ClassWriter createClassFile(String destinationFolderName, String className) throws IOException {
        return new JavaClassWriter(destinationFolderName, className, this);
    }

    @Override
    protected InterfaceWriter createInterfaceFile(File folder, String className) throws IOException {
        return new JavaClassWriter(folder, className, this);
    }

    @Override
    protected InterfaceWriter createInterfaceFile(String destinationFolderName, String className) throws IOException {
        return new JavaClassWriter(destinationFolderName, className, this);
    }
}
