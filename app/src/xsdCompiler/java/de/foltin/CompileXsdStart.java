/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2009 Christian Foltin and others.
 *
 *See COPYING for Details
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Created on 16.06.2009
 */
/*$Id: CompileXsdStart.java,v 1.1.2.1 2009/07/17 19:17:41 christianfoltin Exp $*/

package de.foltin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author foltin
 * 
 */
public class CompileXsdStart extends DefaultHandler {
	public static final String FREEMIND_PACKAGE = "freemind.controller.actions.generated.instance";
	private static final String KEY_PACKAGE = "000_KEY_PACKAGE";
	private static final String FILE_START = "010_start";
	private static final String KEY_IMPORT_ARRAY_LIST = "020_import_array_list";
	private static final String KEY_CLASS_START = "030_CLASS_START";
	private static final String KEY_CLASS_EXTENSION = "040_CLASS_EXTENSION";
	private static final String KEY_CLASS_IMPL = "045_CLASS_IMPL";
	private static final String KEY_CLASS_START2 = "050_CLASS_START2";
	private static final String KEY_CLASS_CONSTANTS = "051_CONSTANTS";
	private static final String KEY_CLASS_MIXED = "055_CLASS_MIXED";
	private static final String KEY_CLASS_PRIVATE_MEMBERS = "060_PRIVATE_MEMBERS";
	private static final String KEY_CLASS_GETTERS = "070_Getters";
	private static final String KEY_CLASS_SETTERS = "080_setters";
	private static final String KEY_CLASS_SINGLE_CHOICE = "090_single_choice";
	private static final String KEY_CLASS_MULTIPLE_CHOICES_MEMBERS = "100_choice_members";
	private static final String KEY_CLASS_MULTIPLE_CHOICES_SETGET = "110_choice_setget";
	private static final String KEY_CLASS_SEQUENCE = "120_sequence";
	private static final String KEY_CLASS_END = "500_CLASS_END";

	private final String destinationDir;
	private final InputStream mInputStream;
	private XsdHandler mCurrentHandler;
	private TreeSet<String> mKeyOrder = new TreeSet<>();
	private Map<String, Map<String, String>> mClassMap = new HashMap<>();
	private StringBuilder mBindingXml = new StringBuilder();

	private Map<String, ElementType> mElementMap = new HashMap<>();
	private Map<String, String> mTypeMap = new HashMap<>();

	private enum ElementType {
		SCHEMA,
		COMPLEX_TYPE,
		SEQUENCE,
		CHOICE,
		ATTRIBUTE,
		COMPLEX_CONTENT,
		ELEMENT,
		EXTENSION,
		SIMPLE_TYPE,
		RESTRICTION,
		ENUMERATION,
		GROUP
	}

	private enum InterfaceName {
		MenuCategoryElement,
		PluginSetting,
		PluginActionElement
	}

	public CompileXsdStart(String destinationDir, InputStream pInputStream) {
		this.destinationDir = destinationDir;
		mInputStream = pInputStream;
		mElementMap.put("xs:schema", ElementType.SCHEMA);
		mElementMap.put("xs:complexType", ElementType.COMPLEX_TYPE);
		mElementMap.put("xs:complexContent", ElementType.COMPLEX_CONTENT);
		mElementMap.put("xs:element", ElementType.ELEMENT);
		mElementMap.put("xs:extension", ElementType.EXTENSION);
		mElementMap.put("xs:choice", ElementType.CHOICE);
		mElementMap.put("xs:sequence", ElementType.SEQUENCE);
		mElementMap.put("xs:attribute", ElementType.ATTRIBUTE);
		mElementMap.put("xs:simpleType", ElementType.SIMPLE_TYPE);
		mElementMap.put("xs:restriction", ElementType.RESTRICTION);
		mElementMap.put("xs:enumeration", ElementType.ENUMERATION);
		mElementMap.put("xs:group", ElementType.GROUP);
		mTypeMap.put("xs:long", "long");
		mTypeMap.put("xs:int", "int");
		mTypeMap.put("xs:string", "String");
		mTypeMap.put("xs:boolean", "boolean");
		mTypeMap.put("xs:float", "float");
		mTypeMap.put("xs:double", "double");
	}

	public static void main(String[] args) throws Exception {
		String destinationDir =
				args[0] + File.separatorChar + FREEMIND_PACKAGE.replace('.', File.separatorChar);
		String freemindActionsXsd = args[1];
		CompileXsdStart cXS = new CompileXsdStart(destinationDir,
				new BufferedInputStream(new FileInputStream(freemindActionsXsd)));
		cXS.generate();
		cXS.print();
	}

	private void print() throws IOException {
		File dir = new File(destinationDir);
		dir.mkdirs();
		for (Map.Entry<String, Map<String, String>> classMapEntry : mClassMap.entrySet()) {
			String className = classMapEntry.getKey();
			// special handling for strange group tag.
			if (className == null)
				continue;
			try (FileOutputStream fs = new FileOutputStream(destinationDir + "/" + className + ".java")) {
				Map<String, String> classMap = classMapEntry.getValue();
				for (String orderString : mKeyOrder) {
					if (classMap.containsKey(orderString)) {
						String string = classMap.get(orderString);
						fs.write(string.getBytes());
					}
				}
			}
		}
		// write binding to disk
		try (FileOutputStream fs = new FileOutputStream(destinationDir + "/binding.xml")) {
			fs.write(mBindingXml.toString().getBytes());
		}
		// write interfaces
		final String interfaceTemplate = """
			package %s;

			public interface %s {
			}
			""";
		Arrays.stream(InterfaceName.values()).forEach(interfaceName -> {
			String ifName = interfaceName.name();
			try (FileOutputStream fs = new FileOutputStream(destinationDir + "/" + ifName + ".java")) {
				fs.write(interfaceTemplate.formatted(FREEMIND_PACKAGE, ifName).getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	public void generate() throws ParserConfigurationException, SAXException,
			IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		mCurrentHandler = new XsdHandler(null);
		mBindingXml.setLength(0);
		mBindingXml.append("""
			<?xml version="1.0" encoding="UTF-8"?><binding>
			""");
		// introduce correct marshaling for newlines in strings:
		mBindingXml.append("""
			<format type="java.lang.String" serializer="de.foltin.StringEncoder.encode" deserializer="de.foltin.StringEncoder.decode"/>
			""");
		saxParser.parse(mInputStream, this);
		mBindingXml.append("</binding>\n");
	}

	private class XsdHandler extends DefaultHandler {
		XsdHandler mParent;
		String mClassName = null;
		String mExtendsClassName = null;

		public XsdHandler(XsdHandler pParent) {
			mParent = pParent;
		}

		String getClassName() {
			if (mClassName != null) {
				return mClassName;
			}
			if (mParent != null)
				return mParent.getClassName();
			return null;
		}

		Map<String, String> getClassMap() {
			String className = getClassName();
			return createClass(className);
		}

		protected void appendToClassMap(String key, String value) {
			mKeyOrder.add(key);
			Map<String, String> classMap = getClassMap();

			if (classMap.containsKey(key)) {
				classMap.put(key, classMap.get(key) + value);
			} else {
				classMap.put(key, value);
			}
		}

		protected void addArrayListImport() {
			if (!getClassMap().containsKey(KEY_IMPORT_ARRAY_LIST)) {
				appendToClassMap(KEY_IMPORT_ARRAY_LIST, """
					import java.util.ArrayList;
					import java.util.Collections;
					import java.util.List;

					""");
			}
		}

		String getExtendsClassName() {
			if (mExtendsClassName != null) {
				return mExtendsClassName;
			}
			if (mParent == null) {
				return null;
			}
			return mParent.getExtendsClassName();
		}

		public void startElement(String pName, Attributes pAttributes) {
			// default: do nothing
		}

		@Override
		public void startElement(String pUri, String pLocalName, String pName,
				Attributes pAttributes) throws SAXException {
			super.startElement(pUri, pLocalName, pName, pAttributes);
			ElementType defaultHandlerType;
			if (mElementMap.containsKey(pName)) {
				defaultHandlerType = mElementMap.get(pName);
			} else {
				throw new IllegalArgumentException("Element " + pName
						+ " is not matched.");
			}
			XsdHandler nextHandler = switch (defaultHandlerType) {
				case ELEMENT -> createElementHandler();
				case COMPLEX_TYPE -> new ComplexTypeHandler(this);
				case COMPLEX_CONTENT -> new ComplexContentHandler(this);
				case SCHEMA -> new SchemaHandler(this);
				case SEQUENCE -> new SequenceHandler(this);
				case CHOICE -> new ChoiceHandler(this);
				case EXTENSION -> new ExtensionHandler(this);
				case ATTRIBUTE -> new AttributeHandler(this);
				case ENUMERATION -> new EnumerationHandler(this);
				case GROUP -> new GroupHandler(this);
				default -> new XsdHandler(this);
			};
			mCurrentHandler = nextHandler;
			nextHandler.startElement(pName, pAttributes);
		}

		protected XsdHandler createElementHandler() {
			return new ComplexTypeHandler(this);
		}

		@Override
		public void endElement(String pUri, String pLocalName, String pName)
				throws SAXException {
			super.endElement(pUri, pLocalName, pName);
			mCurrentHandler = mParent;
		}

	}

	private class ExtensionHandler extends XsdHandler {

		public ExtensionHandler(XsdHandler pParent) {
			super(pParent);
		}

		@Override
		public void startElement(String arg0, Attributes arg1) {
			super.startElement(arg0, arg1);
			String base = arg1.getValue("base");
			mExtendsClassName = getNameFromXml(base);
			mKeyOrder.add(KEY_CLASS_EXTENSION);
			getClassMap().put(KEY_CLASS_EXTENSION,
					" extends " + mExtendsClassName);
			mBindingXml.append("""
				    <structure map-as="%s_type"/>
				""".formatted(base));
			// inform parents:
			XsdHandler xsdHandlerHierarchy = this;
			do {
				if (xsdHandlerHierarchy instanceof ComplexTypeHandler complexHandler) {
					complexHandler.mExtendsClassName = mExtendsClassName;
				}
				xsdHandlerHierarchy = xsdHandlerHierarchy.mParent;
			} while (xsdHandlerHierarchy != null);

		}
	}

	private class SchemaHandler extends XsdHandler {

		public SchemaHandler(XsdHandler pParent) {
			super(pParent);
		}

	}

	private class ChoiceHandler extends XsdHandler {
		private boolean isSingleChoice = false;

		public ChoiceHandler(XsdHandler pParent) {
			super(pParent);
		}

		@Override
		protected XsdHandler createElementHandler() {
			return new ChoiceElementHandler(this);
		}

		protected boolean isSingleChoice() {
			return isSingleChoice;
		}

		@Override
		public void startElement(String arg0, Attributes arg1) {
			super.startElement(arg0, arg1);
			if (arg1.getValue("maxOccurs") != null) {
				// single array list:
				isSingleChoice = true;
				String contentType = getContentType();
				appendToClassMap(
					KEY_CLASS_SINGLE_CHOICE, """
					  public void addChoice(%1$s choice) {
					    choiceList.add(choice);
					  }

					  public void addAtChoice(int position, %1$s choice) {
					    choiceList.add(position, choice);
					  }

					  public void setAtChoice(int position, %1$s choice) {
					    choiceList.set(position, choice);
					  }
					  public %1$s getChoice(int index) {
					    return choiceList.get( index );
					  }

					  public int sizeChoiceList() {
					    return choiceList.size();
					  }

					  public void clearChoiceList() {
					    choiceList.clear();
					  }

					  public List<%1$s> getListChoiceList() {
					    return Collections.unmodifiableList(choiceList);
					  }

					  protected ArrayList<%1$s> choiceList = new ArrayList<>();

					""".formatted(contentType));
				addArrayListImport();
				mBindingXml.append("    <collection field='choiceList' ordered='false'>\n");
			}
		}

		private String getContentType() {
			String name = getClassName();
			return switch (name) {
				case "CompoundAction" -> "XmlAction";
				case "MenuCategoryBase" -> InterfaceName.MenuCategoryElement.name();
				case "MenuStructure" -> "MenuCategory";
				case "Patterns" -> "Pattern";
				case "Plugin" -> InterfaceName.PluginSetting.name();
				case "PluginAction" -> InterfaceName.PluginActionElement.name();
				default -> "Object";
			};
		}

		@Override
		public void endElement(String arg0, String arg1, String arg2)
				throws SAXException {
			if (isSingleChoice) {
				mBindingXml.append("    </collection>\n");
			}
			super.endElement(arg0, arg1, arg2);
		}
	}

	private class ChoiceElementHandler extends XsdHandler {
		private boolean mIsSingle;

		public ChoiceElementHandler(XsdHandler pParent) {
			super(pParent);
			if (pParent instanceof ChoiceHandler choiceParent) {
				mIsSingle = choiceParent.isSingleChoice();

			} else {
				throw new IllegalArgumentException(
						"Hmm, parent is not a choice.");
			}
		}

		@Override
		public void startElement(String arg0, Attributes arg1) {
			super.startElement(arg0, arg1);
			String rawName = arg1.getValue("ref");
			String name = getNameFromXml(rawName);
			String memberName = name.substring(0, 1).toLowerCase()
					+ name.substring(1);
			if (mIsSingle) {
				mBindingXml.append("""
					      <structure usage="optional" map-as="%1$s.%2$s"/>
					""".formatted(FREEMIND_PACKAGE, name));
				return;
			}
			// do multiple choices.
			appendToClassMap(KEY_CLASS_MULTIPLE_CHOICES_MEMBERS, """
				  protected %s %s;

				""".formatted(name, memberName));
			appendToClassMap(KEY_CLASS_MULTIPLE_CHOICES_SETGET, """
				  public %1$s get%1$s() {
				    return this.%2$s;
				  }

				""".formatted(name, memberName));
			appendToClassMap(KEY_CLASS_MULTIPLE_CHOICES_SETGET, """
				  public void set%1$s(%1$s value) {
				    this.%2$s = value;
				  }

				""".formatted(name, memberName));
			mBindingXml.append("""
				    <structure field="%3$s" usage="optional" map-as="%1$s.%2$s"/>
				""".formatted(FREEMIND_PACKAGE, name, memberName));
		}

	}

	private class GroupHandler extends XsdHandler {

		public GroupHandler(XsdHandler pParent) {
			super(pParent);
		}

		@Override
		public void startElement(String arg0, String arg1, String arg2,
				Attributes arg3) throws SAXException {
			mCurrentHandler = new GroupHandler(this);
		}
	}

	private class SequenceHandler extends XsdHandler {

		public SequenceHandler(XsdHandler pParent) {
			super(pParent);
		}

		@Override
		protected XsdHandler createElementHandler() {
			return new SequenceElementHandler(this);
		}

	}

	private class SequenceElementHandler extends XsdHandler {

		public SequenceElementHandler(XsdHandler pParent) {
			super(pParent);
		}

		@Override
		public void startElement(String arg0, Attributes arg1) {
			super.startElement(arg0, arg1);
			String rawName = arg1.getValue("name");
			String type = arg1.getValue("type");
			boolean isRef = false;
			if (rawName == null) {
				rawName = arg1.getValue("ref");
				isRef = true;
			}
			String name = getNameFromXml(rawName);
			String memberName = name.substring(0, 1).toLowerCase()
					+ name.substring(1);
			if (isRef) {
				type = name;
			} else {
				type = getType(type);
			}
			String maxOccurs = arg1.getValue("maxOccurs");
			String minOccurs = arg1.getValue("minOccurs");
			if (maxOccurs != null && maxOccurs.trim().equals("1")) {
				// single ref:
				appendToClassMap(KEY_CLASS_MULTIPLE_CHOICES_MEMBERS, """
					  protected %1$s %2$s;

					""".formatted(type, memberName));
				appendToClassMap(KEY_CLASS_MULTIPLE_CHOICES_SETGET, """
					  public %1$s get%2$s() {
					    return this.%3$s;
					  }

					""".formatted(type, name, memberName));
				appendToClassMap(KEY_CLASS_MULTIPLE_CHOICES_SETGET, """
					  public void set%2$s(%1$s value) {
					    this.%3$s = value;
					  }

					""".formatted(type, name, memberName));
				String optReq = "optional";
				if (minOccurs != null && minOccurs.trim().equals("1")) {
					optReq = "required";
				}
				if (isRef) {
					mBindingXml.append("""
						      <structure field="%1$s" usage="%2$s" map-as="%3$s.%4$s"/>
						""".formatted(memberName, optReq, FREEMIND_PACKAGE, type));
				} else {
					mBindingXml.append("""
						      <value name="%1$s" field="%2$s" usage="%3$s"/>
						""".formatted(rawName, memberName, optReq));
					// whitespace='preserve' doesn't work
				}
			} else {
				// list ref:
				appendToClassMap(KEY_CLASS_SEQUENCE, """
					  public void add%1$s(%1$s %2$s) {
					    %2$sList.add(%2$s);
					  }

					  public void addAt%1$s(int position, %1$s %2$s) {
					    %2$sList.add(position, %2$s);
					  }

					  public %1$s get%1$s(int index) {
					    return %2$sList.get( index );
					  }

					  public void removeFrom%1$sElementAt(int index) {
					    %2$sList.remove( index );
					  }

					  public int size%1$sList() {
					    return %2$sList.size();
					  }

					  public void clear%1$sList() {
					    %2$sList.clear();
					  }

					  public List<%1$s> getList%1$sList() {
					    return Collections.unmodifiableList(%2$sList);
					  }

					  protected ArrayList<%1$s> %2$sList = new ArrayList<>();

					""".formatted(name, memberName));
				addArrayListImport();
				mBindingXml.append("""
					    <collection field="%1$sList">
					      <structure map-as="%2$s.%3$s"/>
					    </collection>
					""".formatted(memberName, FREEMIND_PACKAGE, name));
			}
		}

	}

	private class ComplexTypeHandler extends XsdHandler {

		private boolean mIsClassDefinedHere = false;
		private String mRawName;
		private boolean mMixed = false;

		public ComplexTypeHandler(XsdHandler pParent) {
			super(pParent);
		}

		@Override
		public void startElement(String arg0, Attributes arg1) {
			super.startElement(arg0, arg1);

			String mixed = arg1.getValue("mixed");
			if ("true".equals(mixed)) {
				// in case of mixed content (those with additional cdata
				// content), we add a "content" field to the class
				mMixed = true;
			}
			if (getClassName() == null) {
				mRawName = startClass(arg1);
				// make binding:
				mBindingXml.append("""
				  <mapping class="%1$s.%2$s" type-name="%3$s_type" abstract="true">
				""".formatted(FREEMIND_PACKAGE, mClassName, mRawName));
				mIsClassDefinedHere = true;
			}
		}

		/**
		 * @param arg1
		 * @return the class name
		 */
		protected String startClass(Attributes arg1) {
			mKeyOrder.add(FILE_START);
			mKeyOrder.add(KEY_PACKAGE);
			mKeyOrder.add(KEY_CLASS_START);
			mKeyOrder.add(KEY_CLASS_END);
			String rawName = arg1.getValue("name");
			String name = getNameFromXml(rawName);
			Map<String, String> class1 = createClass(name);
			mClassName = name;
			class1.put(FILE_START, "/* " + name + "...*/\n");
			class1.put(KEY_PACKAGE, "package " + FREEMIND_PACKAGE + ";\n\n");
			class1.put(KEY_CLASS_START, "public class " + name);
			addHackingForTypeSafety(name, class1);
			mKeyOrder.add(KEY_CLASS_START2);
			class1.put(KEY_CLASS_START2, " {\n");
			mKeyOrder.add(KEY_CLASS_CONSTANTS);
			class1.put(KEY_CLASS_CONSTANTS, "  /* constants from enums */\n");
			if (mMixed) {
				mKeyOrder.add(KEY_CLASS_MIXED);
				class1.put(KEY_CLASS_MIXED, """
				  public String content;
				  public String getContent() {
				    return content;
				  }
				  public void setContent(String content) {
				    this.content = content;
				  }

				""");
			}
			class1.put(KEY_CLASS_END, "} /* " + name + " */\n");
			return rawName;
		}

		private void addHackingForTypeSafety(String name, Map<String, String> class1) {
			String interfaceName = switch (name) {
				case "MenuCategory", "MenuSubmenu", "MenuAction", "MenuCheckedAction", "MenuRadioAction", "MenuSeparator" -> InterfaceName.MenuCategoryElement.name();
				case "PluginMode", "PluginMenu", "PluginProperty" -> InterfaceName.PluginActionElement.name();
				case "PluginClasspath", "PluginRegistration", "PluginAction", "PluginStrings" -> InterfaceName.PluginSetting.name();
				default -> null;
			};
			if (interfaceName != null) {
				class1.put(KEY_CLASS_IMPL, " implements " + interfaceName);
				mKeyOrder.add(KEY_CLASS_IMPL);
			}
		}

		@Override
		public void endElement(String arg0, String arg1, String arg2)
				throws SAXException {
			if (mIsClassDefinedHere) {
				String extendString = "";
				if (getExtendsClassName() != null) {
					extendString = " extends=\"" + FREEMIND_PACKAGE + "."
							+ getExtendsClassName() + "\"";
				}
				if (mMixed) {
					mBindingXml.append("     <value field='content' style='text'/>\n");
				}
				mBindingXml.append("""
					  </mapping>
					  <mapping name="%1$s"%2$s class="%3$s.%4$s">
					    <structure map-as="%1$s_type"/>
					  </mapping>
					""".formatted(mRawName, extendString, FREEMIND_PACKAGE, mClassName));
			}
			super.endElement(arg0, arg1, arg2);
		}
	}

	private class ComplexContentHandler extends XsdHandler {

		public ComplexContentHandler(XsdHandler pParent) {
			super(pParent);
		}

	}

	private class AttributeHandler extends XsdHandler {

		public AttributeHandler(XsdHandler pParent) {
			super(pParent);
		}

		@Override
		public void startElement(String arg0, Attributes arg1) {
			super.startElement(arg0, arg1);
			String type = arg1.getValue("type");
			type = getType(type);
			String rawName = arg1.getValue("name");
			String usage = arg1.getValue("use");
			String minOccurs = arg1.getValue("minOccurs");
			String name = arg1.getValue("id");
			if (name == null) {
				name = getNameFromXml(rawName);
			}
			String memberName = decapitalizeFirstLetter(name);
			appendToClassMap(KEY_CLASS_PRIVATE_MEMBERS, """
				  protected %s %s;
				""".formatted(type, memberName));
			appendToClassMap(KEY_CLASS_GETTERS, """
				  public %s get%s() {
				    return %s;
				  }
				""".formatted(type, name, memberName));
			appendToClassMap(KEY_CLASS_SETTERS, """
				  public void set%2$s(%1$s value) {
				    this.%3$s = value;
				  }
				""".formatted(type, name, memberName));
			mBindingXml.append("""
				    <value name='%s' field='%s' usage='%s' %s/>
				""".formatted(rawName, memberName,
				("required".equals(usage)) ? "required" : "optional",
				("0".equals(minOccurs)) ? "" : "style='attribute'"));
			// whitespace='preserve' doesn't work
		}

		public String decapitalizeFirstLetter(String name) {
			return name.substring(0, 1).toLowerCase() + name.substring(1);
		}

	}

	private class EnumerationHandler extends XsdHandler {

		public EnumerationHandler(XsdHandler pParent) {
			super(pParent);
		}

		@Override
		public void startElement(String arg0, Attributes arg1) {
			super.startElement(arg0, arg1);
			String val = arg1.getValue("value");
			appendToClassMap(KEY_CLASS_CONSTANTS, """
				  public static final String %s = "%s";
				""".formatted(val.toUpperCase(), val));
		}

	}

		@Override
	public void endElement(String pUri, String pLocalName, String pName)
			throws SAXException {
		mCurrentHandler.endElement(pUri, pLocalName, pName);
	}

	public Map<String, String> createClass(String pName) {
		if (mClassMap.containsKey(pName)) {
			return mClassMap.get(pName);
		}
		Map<String, String> newValue = new HashMap<>();
		mClassMap.put(pName, newValue);
		return newValue;
	}

		@Override
	public void startElement(String pUri, String pLocalName, String pName,
			Attributes pAttributes) throws SAXException {
		mCurrentHandler.startElement(pUri, pLocalName, pName, pAttributes);
	}

	public String firstLetterCapitalized(String text) {
		if (text == null || text.isEmpty()) {
			return text;
		}
		return text.substring(0, 1).toUpperCase()
				+ text.substring(1, text.length());
	}

	private String getNameFromXml(String pXmlString) {
		StringTokenizer st = new StringTokenizer(pXmlString, "_");
		StringBuilder result = new StringBuilder();
		while (st.hasMoreTokens()) {
			result.append(firstLetterCapitalized(st.nextToken()));
		}
		return result.toString();
	}

	private String getType(String type) {
		return mTypeMap.containsKey(type) ? mTypeMap.get(type) : "String";
	}

}
