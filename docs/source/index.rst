===========================================
solution-finder
===========================================

solution-finderとは、テトリスで、条件にあわせた解をみつけたり、確率を計算するためのコマンドラインツールです。
コマンドを組み合わせることで、複雑な条件下での計算もできます。

コマンド一覧
-------------------------------------------

+ **percent** : ある地形からパーフェクトクリアできる確率を計算します。
+ **path** : ある地形からパーフェクトクリアする手順をすべて出力します。
+ **setup** : ある地形から指定したブロックを埋める操作手順をすべて出力します。
+ **ren** : ある地形からRENを続ける手順をすべて出力します。
+ **spin** : ある地形からTスピンできる手順をすべて出力します。
+ **cover** : 指定された場所・条件通りにミノを置くことができる確率を計算します。
+ **util fig** : テト譜をもとに画像を生成します。
+ **util fumen** : 入力されたテト譜を変換して、新たなテト譜を出力します。
+ **util seq** : solution-finderのパターンをツモ順に展開します。
+ **verify kicks** : 設定されたKickテーブルの検証を行います。

詳細は各コマンドのページをご参照ください。


補助GUI
-------------------------------------------

solution-finderは基本的にCLIからコマンドで操作する必要があります。
一方で ``percent`` ``path`` ``util fig`` コマンドについては、solution-finderを操作しやすくするGUIが付属しています。

こちらのGUIは `@kitsune_fuchi (twitter) <https://twitter.com/kitsune_fuchi>`_ さんに作成していただきました。


ダウンロード
-------------------------------------------

https://github.com/knewjade/solution-finder/releases/ からダウンロードしてください。

（補助GUIを利用したい方は ``solution-finder-*-GUI.zip`` を選択してください。）

また、プログラムの実行には `Java8(64bit) <https://java.com/ja/download/>`_ 以上が必要となります。

その後のステップは :doc:`contents/quick_start` をご覧ください。


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
   contents/verify/main
   contents/inputs
   contents/workflow
   contents/caution
   contents/tools
   contents/licenses
   contents/contact


.. * :ref:`genindex`
.. * :ref:`modindex`
.. :ref:`search`
