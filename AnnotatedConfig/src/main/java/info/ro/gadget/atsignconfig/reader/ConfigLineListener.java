package info.ro.gadget.atsignconfig.reader;

/**
 * 読み込んだコンフィグファイルについて、１行ごとのパラメータを
 * 取り扱うためのコールバック。<br>
 * ラムダ式で使えるといいけど、ライブラリ側はラムダを使う予定はありません。たぶん。
 * @author Robert_Ordis
 *
 */
public interface ConfigLineListener {
	/**
	 * コールバックの中身。
	 * @param lineNum TODO
	 * @param name		パラメータ名相当
	 * @param value		値相当
	 * @param comment	コメント相当
	 * @throws Exception	コールバックでこれが投げられたら強制終了です。
	 */
	public void onRead(int lineNum, String name, String value, String comment);
}
