solution-finderドキュメント
===========================================

概要
-------------------------------------------

solution-finderとは、テトリスで「ある地形からパーフェクトできる確率・接着手順」など、条件に従った解を探索するためのツールです。

solution-finderは、探索ツールとして次の特徴を持っています。

* 任意のフィールド・ミノ組み合わせを指定した探索が可能
* 探索時の回転法則はSRSに準拠
* マルチスレッドによる探索に対応
* 実行時にオプションを与えることで「ホールドあり・なし」「ハードドロップのみ」など細かい設定が可能
* フィールドの入力として `連続テト譜エディタ Ver 1.15a <http://fumen.zui.jp>`_ のデータに対応


主な機能
-------------------------------------------

* percent: ある地形からパーフェクトできる確率を計算する
    - 7種のミノ(TIJLSZO) の様々な組み合わせでの探索が可能
    - 先頭3ミノごとのパーフェクト成功確率もツリー形式で表示
    - パーフェクトができないツモ順を表示

* path: ある地形からパーフェクトまでの操作手順をすべて列挙する
    - 指定したミノの組み合わせから、パーフェクトまでの全パターンを列挙してファイルに出力
    - 2種類の結果を列挙して出力
    - 出力フォーマットは、テト譜リンクとCSVに対応

* setup: ある地形から指定したブロックを埋める操作手順をすべて列挙する
    - 指定したミノの組み合わせから、置くことができる全パターンを列挙してファイルに出力
    - ブロックを置いても置かなくても良いマージンエリアの設定が可能

* ren: ある地形からRENが続く手順を列挙する
    - 指定したミノ順から、RENを列挙してファイルに出力

* spin: ある地形からTスピンできる手順を列挙する

* cover: 指定したミノの置き場所通りに置くことができるミノ順をすべて列挙する

* util: solution-finderの機能を補助するユーティリティ
   - fig: テト譜をもとに画像を生成


ダウンロード
-------------------------------------------

ダウンロードは 以下のリンク からお願いします。

https://github.com/knewjade/solution-finder/releases/

※ プログラムの実行には、Java8が実行できる環境が必要です
※ メモリの関係から 64bit版 を推奨します


Fumen Preview (Chrome Extension)
-------------------------------------------

.. |preview_video_001| image:: contents/img/extension/video1.gif
   :scale: 50
.. |preview_setting_001| image:: contents/img/extension/allow_local_access.png
   :scale: 50

solution-finderが出力するHTMLファイルのテト譜を開くことなく確認できるChrome拡張機能を別途公開しています。

|preview_video_001|

`Chrome Web Store <https://chrome.google.com/webstore/detail/fumen-preview/kkmandajpdmjfibomfokgdjcgmbkipef>`_ からインストールすることができます。あわせてご利用ください。

なお、Chrome拡張機能のデフォルトの設定では、ローカルファイルには拡張機能が適用されません。

Fumen Previewの設定画面から「ファイルのURLへのアクセスを許可する」にチェックする必要があります。ご注意ください。

|preview_setting_001|


補助GUI
-------------------------------------------

solution-finderを操作しやすくするGUIを `@kitsune_fuchi (twitter) <https://twitter.com/kitsune_fuchi>`_ さんに作成していただきました。

なお、補助GUIではpercent・path・util figコマンドのみ対応しています。ご注意ください

ダウンロードは 以下のツイートに記載されているリンク先 からお願いします。

* path, percent コマンド用

    - https://twitter.com/kitsune_fuchi/status/868826325770878976

* util fig コマンド用

    - https://twitter.com/kitsune_fuchi/status/871189534746202113



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
   contents/contact


.. * :ref:`genindex`
.. * :ref:`modindex`
.. :ref:`search`
