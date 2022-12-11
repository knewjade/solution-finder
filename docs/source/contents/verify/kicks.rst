============================================================
サブコマンド: verify kicks
============================================================

概要
============================================================

Kickテーブルの検証を行うためのコマンドです。
定義されたオフセットに沿った各ミノの動きを一覧化して確認できます。

コマンドを実行すると、ミノごとに一覧化されたテト譜のリンクが出力されます。
定義ファイルを変更した際は、そのテト譜を一度目視で確認することをおすすめします。

なお、kicksファイルについては :doc:`../inputs` を参照してください。


基本コマンド
============================================================

``java -jar sfinder.jar verify kicks --kicks @srs``

出力サンプル ::

    T (from Spawn):
    https://fumen.zui.jp/?D115@yfA8HeC8wf1eXMAGsrGEAemsCwSVKEyfQpHeSpwft+?tSA03xUEprDeElsKBA0YceEgZAAAvhJN+tSA03xUEprDeEl?sKBA0YceEAbAAAN5tSA03xUEprDeElsKBA0YceEgcAAAtIu?SA03xUEprDeElsKBA0YceEAeAAANIuUA03xUEprDeElsKBA?0YceEgfAsB9+tRA03hAEFq2TAzxgbEl9+CARAAAAd/tRA03?hAEFq2TAzxgbEl9+CASAAAAd6tRA03hAEFq2TAzxgbEl9+C?ATAAAA9IuRA03hAEFq2TAzxgbEl9+CAUAAAAdJuTA03hAEF?q2TAzxgbEl9+CAVAIBAAAPUARkkAAp9RHEP/JYEV5dNESP9?nD
    T (from Right):
    https://fumen.zui.jp/?D115@xfA8IeB8HeA8ofNeXMAGsrGEAeOpCprDeExfQpIeRp?HeQpofF+tSA03xUEprDeElsKBA0YceEgZAAAvhJl+tSA03x?UEprDeElsKBA0YceEAbAAAlDuSA03xUEprDeElsKBA0YceE?gcAAAF0tSA03xUEprDeElsKBA0YceEAeAAAl0tSA03xUEpr?DeElsKBA0YceEgfAAAV+tRA03hAEFq2TAzxgbEl9+CARAAA?A1+tRA03hAEFq2TAzxgbEl9+CASAAAA1DuRA03hAEFq2TAz?xgbEl9+CATAAAAV0tRA03hAEFq2TAzxgbEl9+CAUAAAA10t?RA03hAEFq2TAzxgbEl9+CAVAAAAAAPUARkkAAp9RHEP/JYE?V5dNESP9nD
    T (from Reverse):
    https://fumen.zui.jp/?D115@7fC8HeA8nfleXOAGsrGEAeOpCFSNXEzoBAA7fSpHeQ?pnf9+tSA03xUEprDeElsKBA0YceEgZAAAvhJd/tSA03xUEp?rDeElsKBA0YceEAbAAAd6tSA03xUEprDeElsKBA0YceEgcA?AA9IuSA03xUEprDeElsKBA0YceEAeAAAdJuUA03xUEprDeE?lsKBA0YceEgfAsBt+tRA03hAEFq2TAzxgbEl9+CARAAAAN+?tRA03hAEFq2TAzxgbEl9+CASAAAAN5tRA03hAEFq2TAzxgb?El9+CATAAAAtIuRA03hAEFq2TAzxgbEl9+CAUAAAANIuTA0?3hAEFq2TAzxgbEl9+CAVAIBAAAPUARkkAAp9RHEP/JYEV5d?NESP9nD
    T (from Left):
    https://fumen.zui.jp/?D115@yfA8HeB8IeA8nf9eXLAGsrGEAe+UCFq+CAyfQpHeRp?IeQpnf1+tSA03xUEprDeElsKBA0YceEgZAAAvhJV+tSA03x?UEprDeElsKBA0YceEAbAAAVDuSA03xUEprDeElsKBA0YceE?gcAAA10tSA03xUEprDeElsKBA0YceEAeAAAV0tSA03xUEpr?DeElsKBA0YceEgfAAAl+tRA03hAEFq2TAzxgbEl9+CARAAA?AF+tRA03hAEFq2TAzxgbEl9+CASAAAAFDuRA03hAEFq2TAz?xgbEl9+CATAAAAl0tRA03hAEFq2TAzxgbEl9+CAUAAAAF0t?RA03hAEFq2TAzxgbEl9+CAVAAAAAAPUARkkAAp9RHEP/JYE?V5dNESP9nD
    I (from Spawn):
    https://fumen.zui.jp/?D115@6fD8wfReXMAGsrGEgNmsCwSVKE6fT4wfp+tSA03xUE?prDeElsKBA0YceEgZAAAvhJp9tSA03xUEprDeElsKBA0Yce?EAbAAAJ/tSA03xUEprDeElsKBA0YceEgcAAApCuSA03xUEp?rDeElsKBA0YceEAeAAAJ1tSA03xUEprDeElsKBA0YceEgfA?AAZ+tRA03hAEFq2TAzxgbEl9+CARAAAA59tRA03hAEFq2TA?zxgbEl9+CASAAAAZ/tRA03hAEFq2TAzxgbEl9+CATAAAA5z?tRA03hAEFq2TAzxgbEl9+CAUAAAAZEuRA03hAEFq2TAzxgb?El9+CAVAAAAAAPUARkkAAp9RHEP/JYEV5dNESP9nD
    ...

オプション一覧
============================================================

======== ====================== ======================
short    long                   default
======== ====================== ======================
``-K``   ``--kicks``            srs
======== ====================== ======================


``-K``, ``--kicks`` [default: srs]
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

検証したい回転法則を指定する。

以下から操作方法をひとつ選択する。

* srs (default): SRSに準拠した回転法則。90度回転のみ
* @ファイル名: ``kicks/ファイル名.properties`` からKickテーブルを読み込みます ( ``@`` の代わりに ``+`` も利用可能です)

なお、kicksファイルのフォーマットは :doc:`../inputs` を参照してください。
