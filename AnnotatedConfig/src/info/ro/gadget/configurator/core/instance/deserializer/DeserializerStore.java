package info.ro.gadget.configurator.core.instance.deserializer;

import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

//まだ途中。
public class DeserializerStore {
	
	Map<Class<?>, AcDeserializer> map = new HashMap<Class<?>, AcDeserializer>();
	
	 private static final Map<Class<?>, Class<?>> PRIMITIVES_TO_WRAPPERS = new HashMap<Class<?>, Class<?>>();
	 static {
		 PRIMITIVES_TO_WRAPPERS.put(boolean.class, Boolean.class);
		 PRIMITIVES_TO_WRAPPERS.put(byte.class, Byte.class);
		 PRIMITIVES_TO_WRAPPERS.put(char.class, Character.class);
		 PRIMITIVES_TO_WRAPPERS.put(double.class, Double.class);
		 PRIMITIVES_TO_WRAPPERS.put(float.class, Float.class);
		 PRIMITIVES_TO_WRAPPERS.put(int.class, Integer.class);
		 PRIMITIVES_TO_WRAPPERS.put(long.class, Long.class);
		 PRIMITIVES_TO_WRAPPERS.put(short.class, Short.class);
		 PRIMITIVES_TO_WRAPPERS.put(void.class, Void.class);
	 }
	
	static private long presetLong(String s) {
		final Pattern hexPattern = Pattern.compile("^(-?)0x([0-9a-fA-F]+)$");
		int baseNum = 10;
		s = s.trim();
		if(hexPattern.matcher(s).find()) {
			s = s.replace("0x", "");
			baseNum = 16;
		}
		return Long.parseLong(s, baseNum);
	}
	
	//プリミティブ型への対応（char単体, voidは今のところ浮かんでない）
	private void prepareForPrimitives() {
		
		//文字列系統
		//String→そのまま配置
		map.put(String.class, new AcDeserializer() {
			@Override
			public String deserialize(String s) throws Exception {
				// TODO Auto-generated method stub
				return s;
			}
		});
		//char[]
		map.put(char[].class, new AcDeserializer(){
			@Override
			public char[] deserialize(String s) throws Exception {
				return s.toCharArray();
			}
		});
		//Character→無理。
		
		//整数系統。遊び心で16進表記にも対応。
		//Byte/byte
		map.put(Byte.class, new AcDeserializer() {
			@Override
			public Byte deserialize(String s) throws Exception {
				// TODO Auto-generated method stub
				return (byte)presetLong(s);
			}
		});
		
		//Short/short
		map.put(Short.class, new AcDeserializer() {
			@Override
			public Short deserialize(String s) throws Exception {
				// TODO Auto-generated method stub
				return (short)presetLong(s);
			}
		});
		
		//Integer/int
		map.put(Integer.class, new AcDeserializer() {
			@Override
			public Integer deserialize(String s) throws Exception {
				// TODO Auto-generated method stub
				s = s.trim();
				return (int)presetLong(s);
			}
		});
		//Long/long
		map.put(Long.class, new AcDeserializer() {
			@Override
			public Long deserialize(String s) throws Exception {
				// TODO Auto-generated method stub
				s = s.trim();
				return (long)presetLong(s);
			}
		});
		
		//実数系統
		//Float/float
		map.put(Float.class, new AcDeserializer() {
			@Override
			public Float deserialize(String s) throws Exception {
				// TODO Auto-generated method stub
				s = s.trim();
				return Float.parseFloat(s);
			}
		});
		//Double/double
		map.put(Double.class, new AcDeserializer() {
			@Override
			public Double deserialize(String s) throws Exception {
				// TODO Auto-generated method stub
				s = s.trim();
				return Double.parseDouble(s);
			}
		});
		
		//その他
		//Boolean/boolean→"1"と入力してもtrueとしてみる。
		map.put(Boolean.class, new AcDeserializer() {
			@Override
			public Boolean deserialize(String s) throws Exception {
				s = s.trim();
				boolean ret = Boolean.parseBoolean(s);
				if(!ret) {
					ret = Integer.parseInt(s) == 1;
				}
				return ret;
			}
		});
	}
	
	private void prepareWellKnowns() {
		map.put(InetAddress.class, new AcDeserializer() {
			@Override
			public Object deserialize(String s) throws Exception {
				// TODO Auto-generated method stub
				s = s.trim();
				return InetAddress.getByName(s);
			}
		});
	}
	
	public DeserializerStore() {
		this.prepareForPrimitives();
		this.prepareWellKnowns();
	}
	
	public void setDeserializer(Class<?> clazz, AcDeserializer d) {
		if(clazz.isPrimitive()) {
			clazz = PRIMITIVES_TO_WRAPPERS.get(clazz);
		}
		this.map.put(clazz, d);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public AcDeserializer getDeserializer(Class<?> clazz) throws NoSuchMethodException, SecurityException {
		if(clazz.isPrimitive()) {
			clazz = PRIMITIVES_TO_WRAPPERS.get(clazz);
		}
		AcDeserializer ret = this.map.getOrDefault(clazz, null);
		
		if(ret == null) {
			ret = new AcDefaultDeserializer(clazz);
			this.map.put(clazz, ret);
		}
		return ret;
	}
	
	
}
