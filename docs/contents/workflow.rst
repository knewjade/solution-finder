============================================================
ワークフロー
============================================================

このページについて
============================================================

solution-finderのコマンドを組み合わせたサンプルコマンドを記載します。


TSD→パフェの確率を求める [PowerShell]
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

ある地形からのパフェ手順をすべて求めて、さらにその中からパフェを取るまでにTSDを打つことができる確率を求めます。

sfinder.jarがあるディレクトリをPowerShellで開き、次のコマンドを実行してください。

::

  $pattern = "*p7"
  java -jar sfinder.jar path -t v115@zgA8GeC8GeE8EeD8DeG8AeF8JeAgH -c 5 -p $pattern -s yes -f csv -k solution
  $fumens = Select-String -Path output\path.csv -Pattern "v115@[a-zA-z0-9+/?]+" -AllMatches -Encoding default | %{$_.Matches} | %{$_.Value }| % -Begin {$str=""} {$str+=$_+" "} -End{$str}
  java -jar sfinder.jar cover -t $fumens -p $pattern --mode tsd


.. note::

  - `ガムシロ積み3巡目のパフェ <https://tetris-matome.com/gamushiro/>`_ のため、5ラインパフェ(``-c 5``)となっています
  - ``path -s yes`` で出力されるテト譜をページごとに分割しています
  - ``path.csv`` から ``Select-String`` でテト譜を正規表現で抽出しています。そのため、解はunique相当になります
  - もしminimalから抽出したい場合は、HTMLファイルからすべての解をまとめたテト譜を削除して、読み込むファイルを変更すれば、同じコマンドで抽出できます。ただし、minimalではTスピンできる解が省略される可能性もあるため、uniqueと結果が異なる可能性もあるのでご注意ください。
  - ``cover --mode tsd`` でパフェの途中でTSDできる確率を求めています