============================================================
プロパティファイル
============================================================

solution-finderには、プロパティファイルから入力する設定がいくつか存在します。
それらのファイルのフォーマットについて説明します。

kicks
============================================================

ローテーションシステムのKickテーブルをカスタムすることができます。
なおここで設定したKicksは、各コマンドで ``--kicks @ファイル名`` で参照可能です（``--kicks @srs`` を指定すると ``kicks/srs.properties`` がロードされます）。

``verify kicks`` コマンドで、各ミノの動きを一覧化して確認できます。設定後は、一度目視で確認することをおすすめします。

デフォルトで用意されているプロパティファイルは以下の通りです。

* **@srs** : SRSに準拠したKicks。90回転のみ。
* **@nullpomino180** : SRSに、180度回転として `Nullpomino <https://github.com/nullpomino/nullpomino>`_ のStandard Wallkickを加えたKicks

サンプル ::

    L.NE=(0,0)(-1,0)(-1,+1)(0,-2)(-1,-2)
    L.ES=(0,0)(+1,0)(+1,-1)(0,+2)(+1,+2)
    ...
    J.NE=&L.NE
    J.ES=&L.ES
    ...
    T.NE=(0,0)(-1,0)(-1,+1)(0,-2)(@-1,-2)
    ...

Propertyのキー (e.g. ``L.NE``)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

``(ミノ).(回転前の状態)(回転後の状態)`` を表します。

たとえば ``L.NE`` は ``LミノをNorthから右回転してEastにする`` ときの挙動を定義します。
180度回転も同様で、``L.NS`` のように設定できます。

したがって定義すべきキーの数は、90度回転のみでは ``56`` 、180度回転も有効にする場合は ``84`` になります。
意図しない定義になるのを避けるため、これら以外のキーの数ではエラーとなるのでご注意ください。

オフセット (e.g. ``(0,0)(-1,0)(-1,+1)(0,-2)(-1,-2)``)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

``(xの移動量, yの移動量)`` を表し、回転時のテストパターンを定義します。回転時のテストは前から順番に適用され、移動可能なところに移動します。すべてのテストパターンに失敗した場合は回転されません。

たとえば ``T.NE=(0,0)(-1,0)(-1,+1)(0,-2)(-1,-2)`` は、 `こちらのテト譜 <https://fumen.zui.jp/?v115@yfA8HeC8wf1eXMAGsrGEAemsCwSVKEyfQpHeSpwft+?tZAy3WeD0ohbEPhQ5DHt2TAzxgbEl9+CARAAAAvhJN+tZAy?3WeD0ohbEPhQ5DHt2TAzxgbEl9+CASAAAAN5tZAy3WeD0oh?bEPhQ5DHt2TAzxgbEl9+CATAAAAtIuZAy3WeD0ohbEPhQ5D?Ht2TAzxgbEl9+CAUAAAANIubAy3WeD0ohbEPhQ5DHt2TAzx?gbEl9+CAVAIBA9+tYAy3WeD0ohbEPBjrDGPVABhA1rDT/Z5?Ad/tYAy3WeD0ohbEPBjrDGPVABhA1rDT/x8Ad6tYAy3WeD0?ohbEPBjrDGPVABhA1rDT/JAB9IuYAy3WeD0ohbEPBjrDGPV?ABhA1rDT/hDBdJuaAy3WeD0ohbEPBjrDGPVABhA1rDT/5GB?AwAAAAAPUARkkAAp9RHEP/JYEV5dNESP9nD>`_ のような動きになります。

オフセットを設定するときの注意として、「移動量には、回転軸を中心に回転したミノの移動量を記述すること」を意識する必要があります。
solution-finderの回転軸はSRSに準拠しています。
そのため、たとえばOミノに対して ``(0,0)`` を設定しても動いてしまいます。
SRSやその回転軸については `Harddrop SRS#How_Guideline_SRS_Really_Works <https://harddrop.com/wiki/SRS#How_Guideline_SRS_Really_Works>`_ がとても参考になるので、こちらを参照していただくことをおすすめします。


@ : T-Spin時にMiniからRegularに昇格するテストパターン (e.g. ``(@-1,-2)``)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

solution-finderでは次のような擬似アルゴリズムでTスピンを判定しています。

::

    if (Tの隅のブロックが2つ以下)
        return NO_TSPIN

    if (Tの凸側の2つの隅が埋まっている)
        return TSPIN_REGULAR

    if (TSTのような特別にREGULARとなる動きである)  // ★
        return TSPIN_REGULAR

    return TSPIN_MINI

この設定の ``@`` がついているテストパターンでは、★の判定で `true` となります。
したがって、SRSにあわせた設定では ``T.NE`` ``T.SW`` ``T.NW`` ``T.SE`` の第5テストパターンに ``@`` が付与されています。
これによって、一般的なTSTの動きがT-Spin Miniではなく、T-Spin Regularで判定されるようになります。


& : 他のオフセットと同じ設定にする (e.g. ``&L.NE``)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

オフセットの設定は、いくつかのミノの種類で同じになることも多くあります。

その場合は ``&(参照したいパターン)`` で、同じオフセット値を引き継ぐことができます。

-----

theme
============================================================

``util fig`` コマンドで出力する画像の色のテーマをカスタムすることができます。

サンプル ::

    T.normal=#b300b3
    O.normal=#b3b300
    ...
    T.clear=#d900d9
    O.clear=#d9d900
    ...
    T.piece=#ff00ff
    O.piece=#ffff00
    ...
    Empty=#151500
    Border=#000000
    SideFrame=#333333

カラーテーマのプロパティは、以下のルールを基に色を設定します。

*ブロック・ライン*

* ``T,I,O,S,Z,L,J,Gray``: 各ブロックの色
* ``Gray``: せり上がりブロックの色
* ``Empty``: 空白の色
* ``Border``: ブロックまわりの線の色
* ``SideFrame``: フィールド左右のフレームの色
* ``BoxFrame``: ホールド・ネクストボックスの枠の色

*適用されるタイミング*

* ``.normal``: 通常時のブロックの色
* ``.clear``: ラインが揃ったときに強調するための色
* ``.piece``: 操作しているミノを強調するための色
