package info.ro.gadget.atsignconfig.ratconfig;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.ro.gadget.atsignconfig.addition.holder.AtsignConfigHolder;
import info.ro.gadget.atsignconfig.core.AtsignConfigParser;
import info.ro.gadget.atsignconfig.core.exception.AcWrongParamException;
import info.ro.gadget.atsignconfig.core.instance.deserializer.AcDeserializer;
import info.ro.gadget.atsignconfig.reader.ConfigLineListener;
import info.ro.gadget.atsignconfig.reader.ConfigReader;
import info.ro.gadget.atsignconfig.reader.preset.CheapIniReader;
import info.ro.gadget.atsignconfig.reader.preset.CheapPropertiesReader;

public class Main {
	
	
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, AcWrongParamException, IOException {
		// TODO Auto-generated method stub
		
		Logger log = LoggerFactory.getLogger(new Object(){}.getClass().getEnclosingClass());
	
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
			public Timestamp getLastModified(String path) {
				return null;
			}
		}
		//コンフィグをしまっておくホルダーを呼びます
		AtsignConfigHolder holder = new AtsignConfigHolder();
		
		//ホルダーは一つ、パーサーを抱えています（名称未確定）
		AtsignConfigParser parser = holder.getParser();
		
		//INIファイル形式用の読み出し器です
		CheapIniReader iReader = new CheapIniReader();
		
		//propertiesファイルの読み出し器です
		CheapPropertiesReader pReader = new CheapPropertiesReader();

		//このmain関数の中で適当に定義したテスト用の読み出し器もどきです
		TestRatProvider tReader = new TestRatProvider();
		
		//パーサーに、抽象クラス「AbstPart」用のデシリアライザを登録します。
		//今回は具象クラス「ConcretePart」のコンストラクタを用意しました
		//parser.registerDeserializer(AbstPart.class, (s)->{return new ConcretePart(s);});
		parser.registerDeserializer(AbstPart.class, new AcDeserializer () {

			@Override
			public Object deserialize(String s) throws Exception {
				return new ConcretePart(s);
			}
			
		});
		
		//LaboRatクラスに沿って、"ratCnfg.ini"からINIファイル形式用読み出し器を使ってコンフィグを崇徳
		LaboRat iRat = holder.registerConfig("ini", LaboRat.class, "ratCnfg.ini", "", iReader);
		
		//LaboRatクラスに沿って、"example.properties"からproperties形式読み出しでコンフィグを取得します
		LaboRat pRat = holder.registerConfig("prop", LaboRat.class, "example.properties", "", pReader);
		
		//"ratCnfg.ini"から、"head b"セクション内のコンフィグを読み出します
		RatHead iniHead = holder.registerConfig("headOnly", RatHead.class, "ratCnfg.ini", "head b", iReader);
		
		//"example.properties"から、"headSecond"よりしたのドメインの値を読み出します
		RatHead tstHead = holder.registerConfig("testProv", RatHead.class, "", "headSecond", tReader);
		
		//laboRatクラスで記述されるコンフィグを今一度全部読み直し、全部取得します
		Map<String, LaboRat> laboMap = holder.getSpecifiedConfigs(LaboRat.class, true);
		
		//RatHeadクラスのコンフィグをリロード（末尾がtrueなので
		holder.getSpecifiedConfigs(RatHead.class, true);
		
		log.info("reload ratHead");
		holder.reloadConfig("headOnly",  RatHead.class);
	}

}
