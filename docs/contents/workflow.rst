============================================================
ワークフロー
============================================================

このページについて
============================================================

solution-finderのコマンドを組み合わせたサンプルコマンドを記載します。


TSD→パフェの確率を求める [PowerShell]
============================================================

ある地形からのパフェ手順をすべて求めて、さらにその中からパフェを取るまでにTSDを打つことができる確率を求めます。

sfinder.jarがあるディレクトリをPowerShellで開き、次のコマンドを実行してください。

::

  $pattern = "*p7"
  java -jar sfinder.jar path -t v115@zgA8GeC8GeE8EeD8DeG8AeF8JeAgH -c 5 -p $pattern -s yes -f csv -k solution
  $fumens = Select-String -Path output\path.csv -Pattern "v115@[a-zA-z0-9+/?]+" -AllMatches -Encoding default | %{$_.Matches} | %{$_.Value }| % -Begin {$str=""} {$str+=$_+" "} -End{$str}
  java -jar sfinder.jar cover -t $fumens -p $pattern --mode tsd

出力は以下のようになります (一例です)。

::

  <...省略...>

  # Output
  success:
  0.00 % [0/5040]: http://fumen.zui.jp/?v115@zgA8GeC8GeE8EeD8DeG8AeF8JeRGYZAFLDmClcJSAV?DEHBEooRBJoAVBK3TWC0AAAAvhEGiBzlBflBCnBlqB
  0.00 % [0/5040]: http://fumen.zui.jp/?v115@zgA8GeC8GeE8EeD8DeG8AeF8Je5FYZAFLDmClcJSAV?DEHBEooRBJoAVBvHkPCsAAAAvhETnBMrBfqBmlBCsB
  0.00 % [0/5040]: http://fumen.zui.jp/?v115@zgA8GeC8GeE8EeD8DeG8AeF8JeWLYZAFLDmClcJSAV?DEHBEooRBKoAVBvHUWC0AAAAvhETiBUhBflBCnBlqB
  0.00 % [0/5040]: http://fumen.zui.jp/?v115@zgA8GeC8GeE8EeD8DeG8AeF8Je9KYZAFLDmClcJSAV?DEHBEooRBUoAVBKuytC6AAAAvhE+nBRmBzfB3mBUrB

  <...省略...>

  0.00 % [0/5040]: http://fumen.zui.jp/?v115@zgA8GeC8GeE8EeD8DeG8AeF8JeSHYZAFLDmClcJSAV?DEHBEooRBMoAVBzXmPCpAAAAvhEfmBUlBlmBGqBxwB
  0.00 % [0/5040]: http://fumen.zui.jp/?v115@zgA8GeC8GeE8EeD8DeG8AeF8JelKYZAFLDmClcJSAV?DEHBEooRBUoAVBziHgC6AAAAvhE/rBSsBuqBzkBUsB
  12.70 % [640/5040]: http://fumen.zui.jp/?v115@zgA8GeC8GeE8EeD8DeG8AeF8JelKYZAFLDmClcJSAV?DEHBEooRBUoAVBaNUPCpAAAAvhEsrBXrB6sBWvBxvB
  0.00 % [0/5040]: http://fumen.zui.jp/?v115@zgA8GeC8GeE8EeD8DeG8AeF8JexKYZAFLDmClcJSAV?DEHBEooRBJoAVBMtHgC0AAAAvhESsBXmBuqBzpBFsB
  5.71 % [288/5040]: http://fumen.zui.jp/?v115@zgA8GeC8GeE8EeD8DeG8AeF8JelKYZAFLDmClcJSAV?DEHBEooRBUoAVBviPFDpAAAAvhETnB6rBWqB0qBxwB
  >>>
  OR  = 35.00 % [1764/5040]
  AND = 0.00 % [0/5040]

この地形でパターン ``*p7`` の場合、TSDをしながらパフェを取れる確率は ``35.00%`` となります。
なお、表示されているテト譜はあくまでもミノの置き場所であり、TSDするための手順ではないのでご注意ください (ミノの置く順番はツモ順によって変化する)。

たとえば、

::

  5.71 % [288/5040]: http://fumen.zui.jp/?v115@zgA8GeC8GeE8EeD8DeG8AeF8JelKYZAFLDmClcJSAV?DEHBEooRBUoAVBviPFDpAAAAvhETnB6rBWqB0qBxwB

これはミノを `このように <http://fumen.zui.jp/?v115@zgA8g0BtzhC8i0hlRpE8BtglRpD8ywglG8wwF8JeAg?H>`_ 置くと、パターン全体の ``5.71%`` のツモ順でTSDできることを表しています。


.. note::

  - `ガムシロ積み3巡目のパフェ <https://tetris-matome.com/gamushiro/>`_ のため、5ラインパフェ(``-c 5``)となっています
  - ``path -s yes`` で出力されるテト譜をページごとに分割しています
  - ``path.csv`` から ``Select-String`` でテト譜を正規表現で抽出しています。そのため、解はunique相当になります
  - もしminimalから抽出したい場合は、HTMLファイルからすべての解をまとめたテト譜を削除して、読み込むファイルを変更すれば、同じコマンドで抽出できます。ただし、minimalではTスピンできる解が省略される可能性もあるため、uniqueと結果が異なる可能性もあるのでご注意ください。
  - ``cover --mode tsd`` でパフェの途中でTSDできる確率を求めています