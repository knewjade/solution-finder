# solution-finder

テトリスのパーフェクトの成功確率を計算するためのツールです。  
現バージョン(v0.1)では、異なる7種のミノ(ITSZJLO) の組み合わせのみ対応しています。  

ダウンロードは releases からお願いします。
https://github.com/knewjade/solution-finder/releases

詳細は、以下をご参照ください。

# 概要

* 指定した地形で、7種ミノの組み合わせ（最大5040パターン）からパーフェクトできる確率を計算する
* フィールドの変更が可能
* マルチスレッド対応
* CLIの表示のみ対応
* パーフェクトができないツモ順を表示（最大100コまで）
* 実行結果をlast_output.txtに出力

※ Javaが実行できる環境が必要です

# フィールドの変更方法と制限

field.txt に「パーフェクトまでに削除するライン数」と「フィールドの形」を入力してください。

* 1行目: パーフェクトまでに削除するライン数
* 2行目以降: フィールドの形

フィールドの形は、以下の文字で指定してください。

* X : ブロックのある位置
* _ : ブロックのない位置

※ 制限:

例) 左側4列が埋まっていて、右側6列が空いているフィールド (4ラインパーフェクト)
```
4
XXXX______
XXXX______
XXXX______
XXXX______
```

# 起動方法

# Windows

solution-finder-runner.batをダブルクリックして起動してください。

# Mac

ターミナルから以下のコマンドを入力してください

```
cd  {jarファイルのあるディレクトリパス}
java -jar -Xmx1024M solution-finder-0.1.jar
```

# 出力サンプル
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
Using pieces: [I, T, S, Z, J, L, O]

# Initialize / System
Available processors = 4

# Enumerate target
Piece pop count = 7
Searching pattern count = 5040

# Search
  -> Stopwatch start
  -> Stopwatch stop : avg.time = 26295 ms [1 counts]

# Output
success = 99.84% (5032/5040)

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
    ∟ TJS -> 100.0 % 

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

-------------------
Fail pattern (Max. 100)
[I, T, L, J, S, O, Z]
[J, I, S, T, O, L, Z]
[I, J, S, T, O, L, Z]
[S, T, L, O, Z, J, I]
[T, S, L, O, Z, J, I]
[T, L, J, O, Z, I, S]
[L, T, J, O, Z, I, S]
[T, I, L, J, S, O, Z]

# Finalize
done
```
