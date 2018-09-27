package functionalTest;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import TestRatPackage.AbstPart;
import TestRatPackage.ConcretePart;
import TestRatPackage.LaboRat;
import TestRatPackage.RatHead;
import info.ro.gadget.configurator.addition.holder.AnnotatedConfigHolder;
import info.ro.gadget.configurator.core.AnnotatedConfig;
import info.ro.gadget.configurator.core.AnnotatedConfigParser;
import info.ro.gadget.configurator.core.exception.AcWrongParamException;
import info.ro.gadget.configurator.core.instance.AcConfigSetterStore;
import info.ro.gadget.configurator.core.instance.deserializer.AcDeserializer;
import info.ro.gadget.configurator.core.instance.deserializer.DeserializerStore;
import info.ro.gadget.configurator.core.instance.setter.MemberSetter;
import info.ro.gadget.configurator.reader.ConfigLineListener;
import info.ro.gadget.configurator.reader.ConfigReader;
import info.ro.gadget.configurator.reader.preset.CheapIniReader;
import info.ro.gadget.configurator.reader.preset.CheapPropertiesReader;

public class Main {
	
	
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, AcWrongParamException, IOException {
		// TODO Auto-generated method stub
		class TestRatProvider implements ConfigReader{
			public TestRatProvider() {}
			@Override
			public String getDomainSeparator() {
				// TODO Auto-generated method stub
				return ".";
			}

			@Override
			public String getSplitter() {
				// TODO Auto-generated method stub
				return "(?<!\\\\),";
			}

			@Override
			public String getCommentStart() {
				// TODO Auto-generated method stub
				return ";";
			}

			@Override
			public Set<String> readConfig(String path, String section, ConfigLineListener listener) throws IOException {
				// TODO Auto-generated method stub
				if(section.equals("head b") || section.equals("headSecond")) {
					listener.onRead(0, "TESTINT", "120", "こめんと");
					return null;
				}
				Set<String> subSections = new HashSet<String>();
				listener.onRead(0, "INTEGER", "123", "整数値");
				//listener.onRead(2, "INTEGER_L", "12345", "倍長整数値");
				listener.onRead(0, "LONG", "-255", "倍長整数値");
				listener.onRead(0, "AUTODIAL2+", "0120565608", "マルハチベッド");
				listener.onRead(0, "INTLIST", "1,2,3,4,5 ", "整数値リスト");
				listener.onRead(0, "NAMED_LIST", "a,b,c,d", "名前。カンマエスケープあるかも");
				listener.onRead(0, "net", "192.168.2.1", "IP");
				listener.onRead(10, "STR", "[YEAH! GIVEN!]", "comment");
				subSections.add("head b");
				subSections.add("headSecond");
				listener.onRead(100,  "FLOATING", "1.2e-1", "floating point");
				listener.onRead(100,  "FLOATING_D", "1.33333333333333", "floating point");
				
				return subSections;
			}
		}
		AnnotatedConfigHolder holder = new AnnotatedConfigHolder();
		AnnotatedConfigParser parser = holder.getParser();
		//TestRatProvider reader = new TestRatProvider();
		CheapIniReader iReader = new CheapIniReader();
		CheapPropertiesReader pReader = new CheapPropertiesReader();
		TestRatProvider tReader = new TestRatProvider();
		parser.registerDeserializer(AbstPart.class, (s)->{return new ConcretePart(s);});
		LaboRat iRat = holder.registerConfig("ini", LaboRat.class, "ratCnfg.ini", "", iReader);
		LaboRat pRat = holder.registerConfig("prop", LaboRat.class, "example.properties", "", pReader);
		RatHead iniHead = holder.registerConfig("headOnly", RatHead.class, "ratCnfg.ini", "head b", iReader);
		RatHead tstHead = holder.registerConfig("testProv", RatHead.class, "", "headSecond", tReader);
		Map<String, LaboRat> laboMap = holder.getSpecifiedConfigs(LaboRat.class, true);
		holder.getSpecifiedConfigs(RatHead.class, true);
	}

}
