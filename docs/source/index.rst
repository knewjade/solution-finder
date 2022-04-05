===========================================
solution-finder
===========================================

solution-finderとは、テトリスで条件に従った解を探索するコマンドラインツールです。理論的なコマンドを組み合わせることで複雑な条件でも様々な結果を求めることができます。

コマンド一覧
-------------------------------------------

+ **percent** : ある地形からパーフェクトクリアできる確率を計算します。
+ **path** : ある地形からパーフェクトクリアする手順をすべて出力します。
+ **setup** : ある地形から指定したブロックを埋める操作手順をすべて列挙します。
+ **ren** : ある地形からRENを続ける手順をすべて出力します。
+ **spin** : ある地形からTスピンできる手順をすべて出力します。
+ **cover** : 指定された場所・条件通りにミノを置くことができる確率を計算します。
+ **util fig** : テト譜をもとに画像を生成します。
+ **util fumen** : 入力されたテト譜を変換して、新たなテト譜を出力します。
+ **util seq** : solution-finderのパターンをツモ順に展開します。

詳細は各コマンドをご参照ください。


補助GUI
-------------------------------------------

solution-finderはコマンドラインツールなため、基本的にCLIからコマンドで操作します。

一方で ``percent`` ``path`` ``util fig`` コマンドについては、solution-finderを操作しやすくするGUIが付属しています。

こちらのGUIは `@kitsune_fuchi (twitter) <https://twitter.com/kitsune_fuchi>`_ さんに作成していただきました。


ダウンロード
-------------------------------------------

https://github.com/knewjade/solution-finder/releases/ からダウンロードしてください。

（補助GUIを利用したい方は ``solution-finder-*-GUI.zip`` を選択してください。）

また、プログラムの実行には `Java8(64bit) <https://java.com/ja/download/>`_ が必要となります。

その後のステップは :ref:`contents/quick_start` をご覧ください。


.. toctree::
   :maxdepth: 2
   :caption: 目次

   contents/quick_start
   contents/field
   contents/patterns
   contents/command
   contents/percent/main
   contents/path/main
   contents/setup/main
   contents/ren/main
   contents/spin/main
   contents/cover/main
   contents/util/main
   contents/workflow
   contents/caution
   contents/tools
   contents/contact


.. * :ref:`genindex`
.. * :ref:`modindex`
.. :ref:`search`
