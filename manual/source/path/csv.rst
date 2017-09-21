key: none
============================================================

出力されるCSVファイルの1行につき、一連の操作手順を表現します。

例) ::

    J,R,3,3;L,L,2,3;O,0,7,0

このうち、4つごとに1つのミノを置く操作を表しています。そのため、この例では3つのミノを置く操作手順を表しています。

例) ::

    J,R,3,3

この例では「ミノJを右回転させた状態で回転軸が(x=3,y=3)にくるように接着させる」ことを表しています。

従って、この4つが表している操作は次のようになります。

* 1つめの要素 J: ミノの種類
    - TIJLSZOのどれか。

* 2つめの要素 R: 回転方向
    - 0: 出現時の回転方向
    - R: 右回転
    - L: 左回転
    - 2: 裏向き (右回転2回 or 左回転2回)

* 3つめの要素 3: 回転軸のx座標
    - 最も左の列を 0 としたときの列番号

* 4つめの要素 3: 回転軸のy座標
    - 最も下の行を 0 としたときの行番号

この4つの要素が1セットとして、必要な操作回数だけ右にセットが続きます。


key: solution
============================================================

パフェ手順（地形）をキーして、それぞれの手順で対応できるツモを振り分けます。

出力例) ::

    http://fumen.zui.jp/?v115@9gh0R4F8g0R4G8BtH8g0BtG8JeAgWDA6vzBA,JSZ,2,48,ZSJ;ZJS,JZSI;ZLJS;JZTS;SZJO;ZSLJ;ZSJI;ZSOJ;ZJSL;ZTSJ;SZLJ;SZJI;SZOJ;TZJS;IZJS;LZJS;ZISJ;ZSIJ;JZSL;OZJS;ZJLS;ZLSJ;ZJOS;SZIJ;ZJST;ZOJS;ZSJL;JZLS;ZSTJ;ZJIS;ZJSO;SZJL;JZOS;JZST;TZSJ;SZTJ;IZSJ;LZSJ;OZSJ;JZIS;JZSO;ZSJT;ZTJS;ZJSI;ZJTS;ZOSJ;SZJT;ZIJS;ZSJO


各項目は、左から順に以下の通りです。

* テト譜
    - パフェの手順（地形）を表すテト譜

* 使用ミノ
    - その手順で使用するミノの組み合わせ

* 対応ツモ数 (対地形)
    - その手順に持ち込むことができるツモの総数 (入力パターンには依存しません)

* 対応ツモ数 (対パターン)
    - すべての入力パターンの中で、その手順で対応できるツモの総数

* ツモ (対地形)
    - 「対応ツモ数 (対地形)」の一覧

* ツモ (対パターン)
    - 「対応ツモ数 (対パターン)」の一覧


key: pattern
============================================================

ツモをキーして、それぞれのツモで対応できるパフェ手順を振り分けます。

出力例) ::

    ITJZ,2,9gzhF8ywG8g0wwH8i0G8JeAgWDAqedBA;9gwhywF8whh0G8whg0H8whg0wwG8JeAgWDAK+1BA


各項目は、左から順に以下の通りです。

* ツモ
    - 入力パターンから生成されたツモ順

* 対応地形数
    - そのツモからパフェ可能な手順（地形）の総数

* テト譜
    - 「対応地形数」の一覧


key: use
============================================================

使用ミノをキーして、それぞれのミノの組み合わせで対応できるツモやパフェ手順を振り分けます。

出力例) ::

    ILZ,1,64,9gzhF8ilG8BtH8glBtG8JeAgWDA6SdBA,ZSIL;TZIL;LIZJ;ILZT;JZIL;IZLT;SZIL;LIZO;OZIL;ILZJ;IZLJ;ILZO;ZOIL;LZJI;ZLOI;IZLO;IZSL;ZLIS;LZOI;ITZL;TIZL;JIZL;ZLSI;SIZL;OIZL;LZIS;IJZL;ZITL;ZTLI;LZSI;ISZL;ZLIT;ZLIJ;ZILS;ZIJL;ZLIO;IOZL;IZTL;ZJLI;LZIT;ZSLI;LIZS;LZIJ;TZLI;JZLI;SZLI;LZIO;ZIOL;ILZS;OZLI;IZLS;ZTIL;IZJL;ZOLI;ZLTI;ZILT;ZILJ;ZILO;IZOL;ZJIL;LZTI;ZLJI;ZISL;LIZT


各項目は、左から順に以下の通りです。

* 使用ミノ
    - パフェ手順で使用するミノの組み合わせ

* 対応地形数
    - そのミノの組み合わせでパフェ可能な手順（地形）の総数

* 対応ツモ数 (対パターン)
    - すべての入力パターンの中で、そのミノの組み合わせで対応できるツモの総数

* テト譜
    - 「対応地形数」の一覧

* ツモ (対パターン)
    - 「対応ツモ数 (対パターン)」の一覧
