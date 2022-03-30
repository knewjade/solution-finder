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

出力例) ``path -t v115@BhF8CeG8BeH8CeG8JeAgWDAqedBA -p *p4 -f csv -k solution`` ::

    テト譜,使用ミノ,対応ツモ数 (対地形&パターン),対応ツモ数 (対地形),対応ツモ数 (対パターン),ツモ (対地形&パターン),ツモ (対地形),ツモ (対パターン)
    http://fumen.zui.jp/?v115@9gh0R4F8g0R4G8BtH8g0BtG8JeAgWDA6vzBA,JSZ,2,2,48,ZSJ;ZJS,ZSJ;ZJS,JZSI;ZLJS;JZTS;SZJO;ZSLJ;ZSJI;ZSOJ;ZJSL;ZTSJ;SZLJ;SZJI;SZOJ;TZJS;IZJS;LZJS;ZISJ;ZSIJ;JZSL;OZJS;ZJLS;ZLSJ;ZJOS;SZIJ;ZJST;ZOJS;ZSJL;JZLS;ZSTJ;ZJIS;ZJSO;SZJL;JZOS;JZST;TZSJ;SZTJ;IZSJ;LZSJ;OZSJ;JZIS;JZSO;ZSJT;ZTJS;ZJSI;ZJTS;ZOSJ;SZJT;ZIJS;ZSJO


各項目は、左から順に以下の通りです。
以下の 例) は、上記の結果を基づいた説明になります。

* テト譜
    - パフェの手順（地形）を表すテト譜
    - 例)
        * ``http://fumen.zui.jp/?v115@9gh0R4F8g0R4G8BtH8g0BtG8JeAgWDA6vzBA`` その行の対象となるパフェ地形のテト譜リンク

* 使用ミノ
    - その手順で使用するミノの組み合わせ
    - 例)
        * ``JSZ`` JSZの3ミノを使ってパフェをする地形である

* 対応ツモ数 (対地形&パターン)
    - そのパフェ手順で組むことができるツモのうち、入力パターンでカバーされる手順の総数
    - 例) 入力パターンが ``*p4`` で、ホールドありのとき
        * ``2`` JSZの組み合わせでこのパフェができる順番は 2通り。この2通りは、入力パターンの範囲で組むことができる

* 対応ツモ数 (対地形)
    - その手順に持ち込むことができるツモの総数 (入力パターンには依存しません)
    - 例)
        * ``2`` （入力パターンに関係なく）JSZの組み合わせでこのパフェができる順番は 2通り

* 対応ツモ数 (対パターン)
    - すべての入力パターンの中で、その手順で対応できるツモの総数
    - 例) 入力パターンが ``*p4`` で、ホールドありのとき
        * ``48`` 全7p4(840通り)の入力パターンのうち、ホールドを使うことで48通りのツモ順に対応できる

* ツモ (対地形&パターン)
    - 「対応ツモ数 (対地形&パターン)」の一覧
    - 例) 入力パターンが ``*p4`` で、ホールドありのとき
        * ``ZSJ;ZJS`` ZSJ か ZJS の順にミノを置いたときだけパフェでき、入力パターンの範囲で組むことができる

* ツモ (対地形)
    - 「対応ツモ数 (対地形)」の一覧
    - 例)
        * ``ZSJ;ZJS`` ZSJ か ZJS の順にミノを置いたときだけパフェできる

* ツモ (対パターン)
    - 「対応ツモ数 (対パターン)」の一覧
    - 例)
        * ``JZSI;ZLJS;JZTS;...`` 入力パターンに従ったパターンなので4ミノの組み合わせになっている


key: pattern
============================================================

ツモをキーして、それぞれのツモで対応できるパフェ手順を振り分けます。

出力例) ``path -t v115@BhF8CeG8BeH8CeG8JeAgWDAqedBA -p *p4 -f csv -k pattern`` ::

    ツモ,対応地形数,使用ミノ,未使用ミノ,テト譜
    TJSI,4,TJS;TIJ,I;S,v115@9gzhF8ywG8g0wwH8i0G8JeAgWDAqedBA;v115@9gh0R4F8g0R4G8g0wwH8ywG8JeAgWDAUtfBA;v115@9gh0R4F8ywG8g0wwH8g0R4G8JeAgWDA0vzBA;v115@9gQ4ywF8R4wwG8g0Q4H8i0G8JeAgWDAKN2BA

なお、このCSV形式を選択した場合は、percentコマンドのようなパフェ成功確率がログ上に表示されます。

ログ出力例) ::

    perfect clear percent
      -> success = 61.19% (514/840)


各項目は、左から順に以下の通りです。
以下の 例) は、上記の結果を基づいた説明になります。

* ツモ
    - 入力パターンから生成されたツモ順
    - 例)
        * ``TJSI`` その行の対象となるツモ順

* 対応地形数
    - そのツモからパフェ可能な手順（地形）の総数
    - 例)
        * ``4`` TJSIのツモを引いたとき、4つのパフェを作ることができる
* 使用ミノ
    - パフェ可能なミノの組み合わせ一覧
    - 例)
        * ``TJS;TIJ`` TJSIのうち、TJS と TIJ を使ったパフェが存在する

* 未使用ミノ
    - パフェした後に残るミノの組み合わせ一覧
    - 例)
        * ``I;S`` TJSIのうち、I (TJS使用時) と S (TIJ使用時) が残るパフェが存在する

* テト譜
    - 「対応地形数」の一覧
    - 例)
        * ``v115@9gzhF8ywG8g0wwH8i0G8JeAgWDAqedBA;...`` 4つのパフェを表すテト譜のデータ


key: use
============================================================

使用ミノをキーして、それぞれのミノの組み合わせで対応できるツモやパフェ手順を振り分けます。

出力例) ``path -t v115@BhF8CeG8BeH8CeG8JeAgWDAqedBA -p *p4 -f csv -k use`` ::

    使用ミノ,対応地形数,対応ツモ数 (対パターン),テト譜,ツモ (対パターン)
    TIL,2,88,v115@9gzhF8ilG8glwwH8ywG8JeAgWDA0SdBA;v115@9gwhywF8whglwwG8whglH8whhlG8JeAgWDAM+1BA,TZIL;LOTI;ILZT;TOIL;TLOI;LITJ;LTIS;LITO;ILTJ;ITLZ;ILTO;TLIS;LIJT;TILZ;LTSI;ILJT;ITZL;TLSI;TIZL;LIOT;ILOT;LTIJ;JITL;SITL;ZITL;OITL;LITZ;JTLI;LTIO;STLI;ZTLI;ITLS;OTLI;ILTZ;JLIT;ITJL;SLIT;TLIJ;ZLIT;IJTL;OLIT;TILS;LJIT;TIJL;TLIO;ISTL;LIST;LTZI;TJLI;LSIT;IZTL;ILST;ITOL;TSLI;LZIT;IOTL;TLZI;TIOL;TZLI;LOIT;JTIL;STIL;TOLI;ZTIL;LITS;OTIL;LTIZ;JLTI;ITLJ;SLTI;ZLTI;OLTI;ILTS;LTJI;ITLO;LJTI;TILJ;TLIZ;ITSL;TILO;TJIL;LSTI;TLJI;TISL;TSIL;LZTI;LIZT;LTOI


各項目は、左から順に以下の通りです。
以下の 例) は、上記の結果を基づいた説明になります。

* 使用ミノ
    - パフェ手順で使用するミノの組み合わせ
    - 例)
        * ``TIL`` その行の対象となるミノの組み合わせ

* 対応地形数
    - その手順に持ち込むことができるツモの総数 (入力パターンには依存しません)
    - 例)
        * ``2`` （入力パターンに関係なく）TILの組み合わせでこのパフェができる順番は 2通り

* 対応ツモ数 (対パターン)
    - すべての入力パターンの中で、その手順で対応できるツモの総数
    - 例) 入力パターンが ``*p4`` で、ホールドありのとき
        * ``88`` 全7p4(=840通り)の入力パターンのうち、ホールドを使うことで88通りのツモ順に対応できる

* テト譜
    - 「対応地形数」の一覧
    - 例)
        * ``v115@9gzhF8ilG8glwwH8ywG8JeAgWDA0SdBA;...`` 4つのパフェを表すテト譜のデータ

* ツモ (対パターン)
    - 「対応ツモ数 (対パターン)」の一覧
    - 例)
        * ``TZIL;LOTI;ILZT;...`` 入力パターンに従ったパターンなので4ミノの組み合わせになっている
