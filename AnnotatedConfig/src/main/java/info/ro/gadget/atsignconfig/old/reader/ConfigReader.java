package info.ro.gadget.atsignconfig.old.reader;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Set;

/**
 * コンフィグの読み込み部分。
 * readConfigを読んだら、parserから提供されるlistenerに下記の塩梅で情報を提供する。
 * ・なんの名前で
 * ・値としては何と書かれていたか（その後の解釈は外に任せる）
 * ・どういうコメントが添えられているか
 * 
 * 改行アリであるとか、そういったことに関しては具象クラスのほうでやってください。
 * @author Robert_Ordis
 *
 */
public interface ConfigReader {
	
	/**
	 * 例えばpropertiesにて階層的読み込みをサポートするための区切り文字。大体ピリオドとなる。<br>
	 * nullで３階層以上の深い読み込みをサポートしないことを示す。<br>
	 * 同時に、実装者は指定した文字でドメインを区切っていることを保証する<br>
	 * @return
	 */
	public String	getDomainSeparator();
	
	/**
	 * 複数の値を１項目で記述していた場合、どのような記述で値の分割を執り行うか。<br>
	 * たいていカンマ区切り。あとは"\\s*,\\s*"とか？<br>
	 * @return
	 */
	public String	getSplitter();
	
	/**
	 * コメント行の先頭を取得する。propertiesなら#、MSのiniなら;となる。（未使用）
	 * @return
	 */
	public String	getCommentStart();
	
	/**
	 * コンフィグ読み込みを実行する。
	 * @param path			読み込み元ファイル
	 * @param sections		ファイル中の読み込むセクションを指定する。（propertiesなら特定のピリオドで区切られた要素、iniなら角かっこのアレ）
	 * @param listener		１行にて出てきた文字に対して何をするかのコールバック
	 * @return				さらに見つけた下層セクションのパラメータ名（直下のだけ）
	 * @throws Exception	大抵読み込み時に出てきた例外。
	 */
	public Set<String>	 	readConfig(String path, String section, ConfigLineListener listener) throws IOException;
	
	/**
	 * 指定されたファイルの最終更新日時を取得する
	 * @param path			対象の読み込み元ファイル
	 * 
	 * @return				ファイルの最終更新日時。nullの場合はライブラリに必ず再読み込みしてほしい場合や環境変数のような明確なファイルではない場合。
	 * @throws IOException	ファイルが見つからん場合これを投げる
	 */
	public Timestamp		getLastModified(String path) throws IOException;
}
