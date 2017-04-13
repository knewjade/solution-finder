# solution-finder

テトリスのパーフェクトの成功確率を計算するためのツールです。  
7種のミノ(ITSZJLO) の様々な組み合わせでの探索が可能です。 

ダウンロードは releases からお願いします。
https://github.com/knewjade/solution-finder/releases

詳細は、以下をご参照ください。

# 概要

* 任意のミノの組み合わせからパーフェクトできる確率を計算する
* フィールドの変更が可能
* マルチスレッド対応
* CLIの表示のみ対応
* ホールドを利用できる場合のみ対応
* パーフェクトができないツモ順を表示（最大100コまで）
* 実行結果をlast_output.txtに出力

※ Javaが実行できる環境が必要です

# 起動方法

# Windows

solution-finder-runner.batをダブルクリックして起動してください。

# Mac

ターミナルから以下のコマンドを入力してください

```
cd  {jarファイルのあるディレクトリパス}
java -jar -Xmx1024M solution-finder-0.2.jar
```

# フィールドの変更方法と制限

field.txt に「パーフェクトまでに削除するライン数」と「フィールドの形」を入力してください。

* 1行目: パーフェクトまでに削除するライン数
* 2行目以降: フィールドの形

フィールドの形は、以下の文字で指定してください。

* X : ブロックのある位置
* _ : ブロックのない位置

※ 制限：高さは最大12までです。

例) 左側4列が埋まっていて、右側6列が空いているフィールド (4ラインパーフェクト)
```
4
XXXX______
XXXX______
XXXX______
XXXX______
```

# ミノの組み合わせの変更方法

patterns.txtに探索対象とする組み合わせを記述します。  

例えば、```I , [TIJLSZO]p4```と書くと、  
```
ITIJL, ITIJS, ITIJZ, ..., IJSZO, ILSZO
```

のように「先頭I」と 「7種から4つを取り出した順列」 を繋げた 7P4=840 通りを探索します。


### 基本ルール

要素1 , 要素2 , 要素3 , ... とコンマで繋げていきます。  
全体の探索個数は 「要素1の総数 × 要素2の総数 × 要素3の総数 × ...」 となります。

例)

I, T, S, Z   →  ITSZ 1通り  
[SZ] , O, [JL]  →  SOJ, SOL, ZOJ, ZOL 4通り  
L , *  →  LT, LI, LJ, LL, LS, LZ, LO 7通り  

### 要素ルール

* ```I``` : Iのみ : 1通り
* ```[SZLJ]``` : SZJLから1つを選択 : 4通り
* ```[SZLJ]p2``` : SZLJから2つを選択する順列 : 6通り
* ```*``` (アスタリスク) : [TIJLSZO]と同等 : 7通り
* ```*p4``` : [TIJLSZO]p3と同様 : 7P3=210通り


### patterns.txtの書き方

1行につき上記の1つのパターンを記述します。  
なお、複数行に渡って複数個のパターンを書くことができます。  
その場合は、各パターンを足し合わせた探索候補から探索します。  

例) patterns.txtに次の3行が書かれたとき
```
T, I
S, Z
L, O
```
この場合 TI, SZ, LO の3通りを探索します。  

ただし、T と I,J のようにブロック数が違うパターンは並べることができません。


# 出力サンプル

一部、出力値についてコメントを追加してあります。  


```
# Setup Field
XXXXXX____
XXXXXX____
XXXXXX____
XXXXXX____
XXXXXX____
XXXXXX____
XXXXXXXX__
XXXXXXXX__

# Initialize / User-defined
Max clear lines: 8
Searching patterns:
  [TILJSZO]p7

# Initialize / System
Available processors = 8
Need Pieces = 7  ← パフェに必要なミノ数

# Enumerate pieces
Piece pop count = 7  ← 1度の探索で使用するミノ個数。ミノに余裕があれば、ホールドのために Need Pieces + 1 となる。
Searching pattern size (duplicate) = 5040  ← パターン定義ファイルを基に生成されたパターン数。ミノ順が重複して登録されている可能性あり
Searching pattern size ( no dup. ) = 5040  ← 実際に探索するミノ順の総数。ミノ順は重複しない

# Search
  -> Stopwatch start
  -> Stopwatch stop : avg.time = 4137 ms [1 counts]  ← 探索にかかったミリ秒

# Output
success = 99.84% (5032/5040)  ← パフェ成功確率。カッコ内は 成功数/全探索数

Success pattern tree [Head 3 pieces]:
* -> 99.8 %
∟ T -> 99.6 %
  ∟ TI -> 99.2 %
    ∟ TIL -> 95.8 %
    ∟ TIJ -> 100.0 %
    ∟ TIS -> 100.0 %
    ∟ TIZ -> 100.0 %
    ∟ TIO -> 100.0 %
  ∟ TL -> 99.2 %
    ∟ TLI -> 100.0 %
    ∟ TLJ -> 95.8 %
    ∟ TLS -> 100.0 %
    ∟ TLZ -> 100.0 %
    ∟ TLO -> 100.0 %

... 省略 ...

  ∟ OS -> 100.0 %
    ∟ OST -> 100.0 %
    ∟ OSI -> 100.0 %
    ∟ OSL -> 100.0 %
    ∟ OSJ -> 100.0 %
    ∟ OSZ -> 100.0 %
  ∟ OZ -> 100.0 %
    ∟ OZT -> 100.0 %
    ∟ OZI -> 100.0 %
    ∟ OZL -> 100.0 %
    ∟ OZJ -> 100.0 %
    ∟ OZS -> 100.0 %

  ∟ OS -> 100.0 %
    ∟ OST -> 100.0 %
    ∟ OSI -> 100.0 %
    ∟ OSL -> 100.0 %
    ∟ OSJ -> 100.0 %
    ∟ OSZ -> 100.0 %
  ∟ OZ -> 100.0 %
    ∟ OZT -> 100.0 %
    ∟ OZI -> 100.0 %
    ∟ OZL -> 100.0 %
    ∟ OZJ -> 100.0 %
    ∟ OZS -> 100.0 %

-------------------
Fail pattern (Max. 100)  ← パフェができなかったパターンを最大100個まで表示
[T, I, L, J, S, O, Z]
[T, L, J, O, Z, I, S]
[T, S, L, O, Z, J, I]
[S, T, L, O, Z, J, I]
[L, T, J, O, Z, I, S]
[J, I, S, T, O, L, Z]
[I, J, S, T, O, L, Z]
[I, T, L, J, S, O, Z]

# Finalize
done

```
