import entry.EntryPointMain;
import output.HTMLColumn;

import java.util.Optional;

public class DevMain {
    public static void main(String[] args) throws Exception {
//        String command = "path -f csv -k p -t v115@9gF8DeF8DeF8DeF8NeAgH -p T,*p4";
//        String command = "setup -t v115@Pg1hDe1hBeB8FtCeA8FtB8AeB8EtA8BeD8CtA8CeE8?AtB8AeI8AeG8JeAgH -p *p7 -m i -f z";

//        String command = "percent -t http://fumen.zui.jp/?v115@DhD8HeC8HeG8BeB8JeAgH -fc 0 -td 1";
//        String command = "percent -t http://fumen.zui.jp/?v115@DhD8HeC8HeG8BeB8JeAgH -p L,L,S,J,J,T,O -fc 0 -td 1";
//        String command = "percent -t v115@wgF8FeG8CeB8GeC8DeF8GeB8JeAgH -p L,L,Z,S,Z,I,J,I,Z,T,O -c 7 --hold no";
//        String command = "percent -t v115@FfI8AeI8AeI8AeI8BeI8AeI8AeI8AeR8AeI8AeI8Ae?I8BeI8AeI8AeI8AeI8JeAgH -p III* -c 16";
//        String command = "move -t v115@9gA8IeA8IeA8IeA8SeAgH -p [TIO]p2";
//        String command = "move -t v115@zgQ4IeR4CeAtDeglQ4BeBtDeglywAti0Rphlwwzhg0?RpJeAgH -oc yes -c 6 -p I";
//        String command = "path -k solution -f csv -t v115@zgyhGexhHexhGeAtxhC8BeA8BtyhE8AtA8JeAgWBAV?AAAAvhAAAPBAUAAAA -P 2 -p [IJLOS]p5,S --split yes";
//        String command = "path -t v115@9gB8HeC8GeE8EeF8NeAgWMA0no2ANI98AQPcQB";
//        String command = "path -t v115@9gB8EeF8DeG8CeF8DeC8JeAgH -p I,*p3";
//        String command = "path -t v115@QhC8BeA8BeE8AeG8JeAgH -p *!";

//         String command = "setup -p [^T]! --fill i --margin o -t v115@zgdpwhUpxhCe3hAe1hZpJeAgH";  // 14
//        String command = "setup -p [^T]! --fill i --margin o -t v115@zgTpwhYpAeUpzhAe3hQpAeQpzhTpAeUpJeAgH";  // 7
//        String command = "setup -p [^T]! --fill i --margin o -t v115@zgUpwhYpAeTp0hAe3hQpAeQpyhUpAeTpJeAgH";  // 7

//        String command = "setup -p [S]! --fill i --margin o -t v115@8gQpbexhGeQpwhKeAgH";  // -> S only = 1
//        String command = "setup -p [I]! --fill i --margin o -t v115@8gQpIeQpIeQpIeQpIeQpJeAgH";  // -> Last I only = 1
//        String command = "setup -p [I]! --fill i --margin o -t v115@8gQpIeQpIeQpIeQpIeQpJeAgH";  // -> all margin = error
//        String command = "setup -p [I]! --fill i --margin o -t v115@8gQpIeQpIeQpIeQpIewhJeAgH";  // -> Last I only h5 = 1
//        String command = "setup -p [I]! --fill i --margin o -t v115@GhQpIeQpIeQpIewhJeAgH";  // -> Last I only h4 = 1
//        String command = "setup -p [I]! --fill i --margin o -t v115@FhQpIeQpIeQpIewhKeAgH";  // -> x8 I only h4 = 1
//        String command = "setup -p [I]! --fill i --margin o -t v115@6gQpIeQpIeQpIeQpIewhLeAgH";  // -> x7 I only h5 = 1
//        String command = "path -s yes -c 6 -p [^IL]!,*p2 -t v115@wghlwhHeglwhEeA8BtglwhA8DeB8BtwhT8JeAgH";
//        String command = "path -t v115@VgL8FeC8AewwBeI8BeS8BeG8EeD8EeB8JeAgH -c 8 -p Z,T,Z,L,*p3 -f csv -k pattern -r true";

//        String command = "ren --tetfu v115@VgF8DeF8DeF8DeF8DeF8DeF8DeF8DeI8KeAgH --patterns TOLJISZ";
//        String command = "ren -t v115@neF8DeF8DeF8DeF8DeF8DeF8DeF8DeF8DeF8DeF8De?F8DeF8DeF8DeF8DeF8DeF8DeF8DeF8AeL8KeAgH -p tilsojztilsojz";

//        String command = "ren -h";

//        String command = "setup -p [^SZT]! --fill i --margin o -t v115@zgTpwhQpDeTpAeQpDezhAewhDeyhQpAeQpwhCeTpAe?RpMeAgH";
//        String command = "setup -p *! --fill i --margin o -t v115@3gwhHezhGe0hAexhAe4hAe2hJeAgH";
//        String command = "setup -p *! --fill i --margin o -t v115@2gWpCeWpDe0hQpxhAe4hAe2hJeAgH --format csv";
//        String command = "setup -p *! --fill i --margin o -t v115@2gWpCeWpDe0hQpxhAe4hAe2hJeAgH";
//        String command = "setup -p [^SZT]! --fill i --margin o -t v115@zgTpwhQpDeTpAeQpDezhAewhDeyhQpAexhCeTpAeRp?MeAgH";
//        String command = "setup -t v115@zgyhDeC8whQ4FeB8R4FeB8Q4B8EeB8AeB8DeC8JeAg?H -p LZ -f I -F S -c no -np 2";

//        String command = "path -t v115@RhA8GeE8EeB8JeAgH -p LZZSITSO";
//        String command = "path -p J,Z,O,S,L,I,I,J,S,O,Z -t v115@vhAAgH";

//        String command = "spin ";  // 0 solutions
//        String command = "spin -p *! -t v115@HhB8AeH8BeI8AeG8JeAgH -mr 1";  // 2 solutions
//        String command = "spin -p *! -t v115@HhB8AeH8BeI8AeG8JeAgH -r no";  // 1 solutions

//        String command = "spin --tetfu v115@KhB8DeB8BeB8AeI8BeC8JeAgH --patterns *p7 -ft 7";
//        String command = "util fig -c fumen --tetfu v115@BgC8GeC8BeglDeD8AeglDeE8hlCeF8ywAtG8wwBtH8?AtAeI8AeI8AeI8KeAgH -F png -f no";
//        String command = "util fig --tetfu v115@BgC8GeC8BeglDeD8AeglDeE8hlCeF8ywAtG8wwBtH8?AtAeI8AeI8AeI8KeAgH -F png -f no";
//        String command = "util fig -t v115@vhM1OYPIFLDmClcJSAVDEHBEooRBUoAVB6yjPCPebM?CqybgC0uzPCMdFgCM9KxCKejxCqn9VCTnPFDJHWWCzyTxCv?fbMC6uHgCzS9wCz33LCvCegCpintCaeHgCzCmFDMubgC6/V?xCa+LgCzfzPCvCOMCzizPCU3LMCMXNPCUtbMCsuHgCad9wC?zvKWC6C+tCauaPCzfrgCpOUPCzijxCJHMgC0vLMCa3TxCp/?TFDz/dgCpizFDvCOMCKNOMCvHkPCMnLuCseNPCa9aPC0Slt?CMXNPC0nltCUHUFDK+TPC0HztC6OstCKXltCvP9VC6e9VCz?+aPC6+bgCs33LCK+VWCvHMMCU3TWC6fLuCPe/VCUePFDM9K?xCa+lPCM3TWCTnbMCqubMCqOMgCzn9wCzyaPC0fbMCTHEWC?pfjxCT3TxCpXUPCv3/VCU3LMCUnjWCpvbgCzCOMCq3bMCzC?egCqHLWCp+jPC0HDMCMt/VC6/9tCJ3KWCp/9tC6intCvubM?CMXltCqubMCTnjWCq+ytCKejWC0CmFDpvbgCUe/VCTnzFDz?iHgCpO8LC6/9tCv+TWCPezPCqHLWCp+LMCqXegCMtbMCsvy?tC0uPFDTX9VCJNegCsfzPCaHOMCzC+tC6/7LCPtbMCv/DxC?KnjWCJN+tC6C2BA0qByxBvrBTnBmiBckBZVB2dBKgBvhBzT?BtVBVgwhwwEeRpCewwCeg0EewSAeQ4BeRpBeQaCeQ4DeQpB?eglBexSCewhhlDeAPAewhAewhAewSAeRaBeSpBtwhgWh0A8?Je5RBvhB9SBcHBmfAtHeBtFewhwwAtwhAewwAeRpAewhxww?Sxwg0RpAewhwwgWwSQ4wwi0BeQagWAeQ4glg0RpAeQpQawS?whhlwSAeQpAeQpAeglQpAeglxSAPAeAtwhRpwSAeCPAeBth?0xhwSwwglA8APQpBtxhxwg0A8QLRpBtwhi0A8AAEeA8MeKy?AvhLP6ApUBzBB6UBvOBTKBGEBFIBZNB2VB0LBuRB3fBtAeh?0Eeg0Btg0whEej0whi0BewSwhglQLwhwwi0AeglBtglQ4A8?AeC8gHilQ4ilB8QaQpg0Q4whQpilA8AAEeA8CeA8EeAACeA?8EeAACeA8EeAAMe9KBvhJMKBSOBPHBzCBOOBpIBZVBMeBTd?BibBNgilwhRpAeAtAeQ4glRpwhRpBtF8AeI8AeE8i0Q4wSw?wA8AtA8whg0xwQ4wSwwBtneNGBvhAXEB9fR4BewwDeQ4Aew?DCewwilgHAPQpAeAtRpi0Q4wSwwA8At8eAADeA8Mev8AvhT?yCBFFBTFBUCBGDBJiBMdB+eBzYB1gBvcBKaBJiBtpB0rBfq?BOkBznB6oBpUBUgwhdeh0FeglwhAewhEeQpwhCeQ4AeBtDe?APgWBeQpAeAtBeAtg0QLwhA8whBtxwglJeTQBvhEJdB6lB+?mBtnBXdBegwhFeR4AewhRpCeR4wwBeRpCeg0gHgWwwAeRpC?eQagHAeglwhwwwSC8glQ4RpQ4hlAeB8glQ4Qph0AeA8AAQe?ZGBvhG2gB0bBfcBaeBTUBscBFXB3fwhGeRpwhBeywAtAeRp?whBeQ4wwBthlxhBeR4AtR4glwhQ4AeF8xwQ4A8AeSpAtA8x?wQ4A8AewhQpBth0R4B8xhAtwhwDg0Q4AeA8EeAADeA8DeAA?DeA8DeAALe5GBvhQvEBT+AK3AWABFCBcHBUIBFJB6GBJRBT?GB+ZBcWBfZB2cBTZBiWBfgRpilCeRpCeglAtBeQ4Bexwg0A?tglB8wDxwieAAAeA8LeFTBvhGvSB5iBKkB+oBUlBTnBfoBw?gQ4BeglFeR4g0glBtAeQ4AeQpAPQ4h0D8AeA8xhglg0APAt?A8whA8xwwhglAeAACeA8NeZLBvhMlbBCWBTUBOYBdSBpcBH?gBshBSoB1gBeeBTYBpUBsfwhIewhFeRpg0whCewwBeRpg0x?hAeywAeAth0glwhAeR4wwBthlwhA8AeG8Q4A8AeD8xwglQ4?C8QpB8wSwwglR4A8SpA8APhlg0Q4A8xhQpAtAPi0AeA8DeA?ADeA8DeAAMevDBvhGs5A5LB3MB1VBzOBiQB2VBVgg0Jeh0C?eAtRpwhilC8APxwQ47eSSBvhG0LBXNBUHBJnBTjBuiB9ZB2?fwwHexwAeBtDeh0wwDtR4Aeg0Rpg0BtR4glAeg0RpF8AeB8?QpF8AeA8RpA8APAtD8hlQpAtAPBtxhA8glxwglAPAtxhg0A?8glxwAeAADeA8DeAADeA8MeWDBvhBX4AT5A9eR4RpCewwAe?R4g0RpBexwAeBti0Aeh0wwDtR4Aeg0Rpg0BtR4glAeg0RpB?8xhxwAeB8QpA8xhglxwAeA8RpA8APAtilA8hlQpAtAPBtxh?A8glxwglAPAtxhg0A8glxwAeAADeA8DeAADeA8DeA8EeAAC?eA8EeAACeA8EeAACeA8EeAACeA8EeAALeJ1AvhUM6AaxAO3?AF6A67A04Al8A5LBXTBTQBTGBKMBNSB+UB5nB0mBTaBPiBt?nB5gBOlBNgh0FeRpg0whAeQ4DeRpg0whAeR4wwAeg0G8AeB?8AeK8hlE8AexwglQ4A8whC8AexwglQ4A8xhQpA8gHGeA8Ae?AAJeKBBjfglIeglJeglh0FewhQpg0whAeQ4DexhAeQLAeR4?wwAeg0xwglQ4A8whA8AeB8wSwwglQ4A8xhQpA8glGeA8AeA?AA8HeAAYeAACeA8EeAACeA8JeMFBvhO3/AODBPEBtJBzGBq?HBMDBMEBT8Ai9A9KBpABJFBPJBuJBgfh0whEeQ4Aeg0xhEe?R4g0xhAeP8hlQaE8whA8glQ4QaE8xhglQ4QaA8HeAAEeAAC?eA8EeAACeA8GeAAAeA8GeAAAeA8EeA8AeAAGeA8AeAALedw?AvhbzzAc3AZtAO4AC4A/tAM5ATzAv6AN0ApKB+PB0BBfPBT?PBuXBlWB6iBpcBKfBylB3bBTjB+ZBJdBsaB5nB3mB4gR4Be?g0BeAtAeR4whBeg0glBtR4hHAeg0AeB8AtA8wDwhQ4B8glg?0BtwhwDR4A8hlJelbBvhATaBfgRpywR4Beg0RpAtwwR4whB?eg0QawhQahlhHAeg0AexwAtQpwDwhQ4B8glg0BtwhwDR4A8?hlAACeA8EeAACeA8OetYBvhKMZBOhBikBCmB+nBNoBZkBzp?B0lBPsBUsBugBtCewhAeBtAeQ4BtwwAeD8AeI8AeI8AeBtC?8Q4A8BtAewhBtQpA8JeCWBggilAeBtCewhAehlAeQpAeAtw?wAeQ4g0BtAewhBtQpA8heA8AANeNLBBgwwJewwIewShlAeB?tCeAPAehlAeQpAeAtwwAeQ4g0BtAewhBtQpA8heA8AAXeGP?B3fwwJewwIewShlAeBti0APAehlAeQpAegWQaAeQ4g0BtAe?whBtQpglheA8AASeA8AAMeTABvhBZXB/LBtfQ4IeR4HewwQ?4FeRpgWwwFeRpgWQLhlAeBti0APQLBtAeQ4BtglSpC8AeB8?xwQpi0A8APAtilQ4g0BtA8whAPAtQpglneTEBvhRONBpAB3?MBiMB0BBpSBidB/bB1iBTQBOZBMYB9hBFhBvmBTlBasB0sB?5gBtDeRpQ4hlBtAeI8AeF8BtA8AeB8xwwhh0BtKemYBvhGp?oBWrBpjBKpBzpBUrBXqBrgR4EewhAeR4BtDewhG8AeI8AeD?8xhC8AeA8Q4A8xhBtB8AeA8Q4JeNVBVgwwJewwR4EewhAeg?lAewSAtEeQpxhBtB8AeA8Q4jeAAA8Le5iBvhGznBvmBSjB0?aBmVB8bBXZBBgi0AeAtCeR4Btg0BtBeR4glwwCtQ4Beilxw?T4AeRpwhQaxhAtAeQ4AeQpAPgHBtglBtA8Aexhg0QpCtwhB?8h0gWRpzhA8xwQaQpxhBtwhA8xwQaFeA8BeAAJeT3AvhKJT?BtXBKYBlWBpoBGrBOiB6tBMlB3fBThBlgh0CeR4AtRpg0Ce?R4BtRpAegHhlQpAPwSglRpAPBeglBPxhglQpAPAegWAexwA?Pilg0B8g0xwQLRpglg0B8g0JetiBvhAcaBggAtHeAtEeh0C?eQpQ4AtRpAegHBeQaAPgWwhglQpAPwwAeglxwQLRpglg0Qp?A8g0LeA8DeAALeFeBvhE+eBJgBSrBXmBTgBrgRpHeRpR4Ce?g0AeAtgWAtQ4glCeg0B8xwxhA8AeA8glA8AtQ4whwDg0C8g?lDeAABeA8LePYBvhDTMBcaBZLBuLBBgwhh0IewSQpKeQ4Ee?gWDeQ4CeQpCeQ4Deg0BegWAtAeQLwDCeBtQ4xhg0A8AeA8g?lDeAABeA8feldBvhJMeB6dBZmB2fBirBTiBFtB0fB3mB5lB?zgBtAewhFeglAeAtBeR4DehlAeQ4AeAPQpBeQLCexDDeQ4h?lQ4B8xwAeA8JePWBvhBNtBziB4fQ4IeR4GeBtQ4whCeRpAe?g0BtwhAeR4RpAei0whR4RpwwAewhQag0whBeRpxwA8xhAeF?8BtwhQaC8xwA8glBtQaA8xhxwA8ilQaxhxwQpA8Q4hlQ4A8?AexwRpJeO8Ajfh0IewhJeQ4GegWwSAewhCeRpAeglAewSBe?R4DehlAeQ4AeAPAeglAeQLCexDCewwQ4hlQaB8xwRpgeA8A?eAAFeAABeA8NeNDBvhWi/As5ApFB/HBzJB6KBaNBuNB5bB2?kB0gBveBtiBzbB/fBlmBMnBpeBaqBmiBzfBOfB6gBLgh0hl?Eewhg0RpglBei0whg0RpglCeAtg0whQ4hlRpAeBtQ4yhAew?hQpAeAtwwQ4g0glwSwwg0B8ilQ4glwSwwg0C8AtglQ4whgW?g0xwA8BtwhQ4whwDg0xwA8AtQpxhJelSBvhLMPBvXBTYB9X?B5kBTpBKqBUsBOrB3kB5lBTiBigwhBeRpCeR4whh0RpBeR4?glwhg0BtCeRpglAeg0wwxhAeAtA8xhQ4gHglxwB8xhg0Q4g?HBtC8xwg0Q4gHQpBtA8AtJe3NBofR4FewhR4RpCeR4whh0R?pBeR4glwhg0BtCeRpglwhg0wwBtAeAtD8AexhF8Q4wDwhxw?C8xhQ4gHglxwB8xhgWQ4glBtC8xwgWQ4glQpBtA8AtBeAAA?eA8GeAAAeA8GeAAAeA8Oei3AvhEFFB28A04AsyAZoATewhI?ewhIewhAeAtGewhBtGeg0CtFewwh0BtR4CewhhlwhR4RpBe?whQ4Qpwhh0RpBeQ4BtA8AeE8glCtAeE8ilAtAPxhC8h0gWQ?4xhxwB8g0whwDQ4hlxwB8BeAAAeA8GeAAAeA8GeAAAeA8Ge?A8CeAAEeA8CeAAEeA8CeAAEeA8CeAAEeA8CeAAMe/hAvhFK?sApnAdsAO0AM5AT1AnewhIewhEeRpBewhAeAtBeAtRpwwAe?whBtAeBtQ4xwwhB8AeG8Q4A8AeG8Q4A8AeC8xwB8Q4A8APB?8AtxwQpA8Q4AtAPA8BtwhRpQ4BeA8CeAAEeA8CeAAEeA8Ce?AAEeA8CeAAEeA8CeAAHeAAA8HeAAA8HeAAA8HeAAA8HeAAA?8MezaAvhCpYAtZAfdAkeQ4AexhwwFeQ4CewwRpAeQpAPCeR?awSwhBtglgWAeQ4AtAPA8BtwhRpQ4peA8CeAAveAAA8teAA?AeA8Ne6WAvhIlaAOcAMdAJuATtA3uAOzAKtAMxAneglHeAt?glGeBthlh0DeBtRpg0R4Aeh0Btg0E8AeB8Atg0E8AeA8Bth?0hlB8AeA8BtxwglxhA8gHglBtOeAAA8HeAAA8HeAAA8HeAA?A8PeAAAeA8keA8AeAANeChAvhLFgAOrAXpAJ1Az6ATzApNB?GKB9LBvSB0SBiQBLgilGeglwwRpBtAel0AeF8g0QpxwAtAP?A8ilCeA8AeAA1eiJBvhAGKBqfi0ilCeilg0C8AeK8AeI8Ae?A8ili0B8Aei0glCeA8AeAAJf30AvhWTtAlzA84ApmBSUBpm?BXeBOiBTaBNgB0WBpmBvnBToBKkBWlBlrBUlBbaB+oBnmB8?nBqlBVgRpAeglFeRpAeglDeAtg0AeBthlR4Btg0gli0BeR4?RpxwA8g0AeE8xwA8g0AeC8AtglA8Btg0gWxhBtglg0ilAeA?8xhxwJeZHBvhESTBNNBdOBUPBGPBlfwhCewwi0BewhAeywB?tg0AeBthlR4Btg0gli0BeR4RpD8AeG8Q4A8AeA8QpilB8Q4?A8QLRpBtglA8Btg0gWxhBtglg0ilB8xhwSwwDeA8CeAAEeA?8CeAAEeA8CeAAEeA8CeAAKe3vAvhDTsAN3A5zAG1A0ewhAe?i0RpCewhAeR4g0RpwwAexhR4wwi0gli0BeR4RpD8AeH8Q4A?eilxwC8Q4AexhglxwQpA8R4wDwhQpilg0ilB8xhwSwwDeA8?CeAAEeA8CeAAEeA8CeAAEeA8CeAAFeAABeA8FeAABeA8FeA?ABeA8FeAABeA8FeAABeA8KeSYAvhK/eAZ0A0tATjAXnAa0A?OzAz0AZ0At5AU4AQfBtAewhwwDeRpBtwhxwAeR4H8AeB8Bt?A8Q4QpB8AeA8xwBtQ4RpA8wDwhYeAABeA8FeAABeA8FeAAB?eA8UeAADeA8DeAADeA8DeAADeA8NeijAvhO8kA2eA9rAJAB?z5ANFBvFBvEBCDBzBBc8AGFBpXBGcBTbBggAtRpFeBtRpGe?AtxwF8APAtxwF8JeA8AAHeA8AAReSZBvhAMPBKgAtHeAtBe?AtRpEegWAtFeglgWAeAPAtxwC8i0TeA8AASeA8GeAAJe1SB?vhGfNBZVB+dB0cBFZB5fBalBWghlDeywAewhglQ4BtAeg0w?wAtAewhglR4Btg0BtI8AeA8h0C8AeSpA8Q4g0whBtAeglQp?AtA8Q4g0xhAtAPglBtFeAABeA8JePOBvhH0HBZLBTTBmRBT?JB/QB9NBVMBDgwwBewwRpBeQ4wwAexwFeglyhEegHAehWAe?glDeQ4whBtglwhxwA8GeAABeA8jeA8BeAAJeK8AvhE09AvU?BTPBpPBKSB7fglDewhglCeglDewhglAeBthlCewhD8g0A8A?eB8Q4g0C8g0A8AeB8Q4g0A8Bth0AeB8Q4PeA8BeAAFeA8Be?AAFeA8BeAATeJTBvhI2dBtVB/WBzYB6XBcVB+NBuRBcLB4f?AtCeg0DeBtAei0DeAtBeh0GeQ4AeBPEewhQ4whBPAeRpwhB?twhglh0C8AeAtQpxhh0A8xwQaTeAAHeA8AAHeA8JeTKBvhE?NOBvOBJHBNGBXIB3fwwJewwwhAeR4EeQaAeQ4AeQaQ4CeQa?BeRaBeQ4BeBtQ4ilxhA8eeAAHeA8JeA8AARe89AvhM6UBGZ?BpZByiB3dBscBzaBTZBGbBKXB9YBWWBTPBTgRpAeg0BeglB?ewwDeh0BewwHeglQaDexSAehlAeglAtgWAexwglCtxhQ4Je?A8AAIeA8BeAAOesNBvhCZaBCZBAAA";
//        String command = "util fig -t v115@vhM1OYPIFLDmClcJSAVDEHBEooRBUoAVB6yjPCPebM?CqybgC0uzPCMdFgCM9KxCKejxCqn9VCTnPFDJHWWCzyTxCv?fbMC6uHgCzS9wCz33LCvCegCpintCaeHgCzCmFDMubgC6/V?xCa+LgCzfzPCvCOMCzizPCU3LMCMXNPCUtbMCsuHgCad9wC?zvKWC6C+tCauaPCzfrgCpOUPCzijxCJHMgC0vLMCa3TxCp/?TFDz/dgCpizFDvCOMCKNOMCvHkPCMnLuCseNPCa9aPC0Slt?CMXNPC0nltCUHUFDK+TPC0HztC6OstCKXltCvP9VC6e9VCz?+aPC6+bgCs33LCK+VWCvHMMCU3TWC6fLuCPe/VCUePFDM9K?xCa+lPCM3TWCTnbMCqubMCqOMgCzn9wCzyaPC0fbMCTHEWC?pfjxCT3TxCpXUPCv3/VCU3LMCUnjWCpvbgCzCOMCq3bMCzC?egCqHLWCp+jPC0HDMCMt/VC6/9tCJ3KWCp/9tC6intCvubM?CMXltCqubMCTnjWCq+ytCKejWC0CmFDpvbgCUe/VCTnzFDz?iHgCpO8LC6/9tCv+TWCPezPCqHLWCp+LMCqXegCMtbMCsvy?tC0uPFDTX9VCJNegCsfzPCaHOMCzC+tC6/7LCPtbMCv/DxC?KnjWCJN+tC6C2BA0qByxBvrBTnBmiBckBZVB2dBKgBvhBzT?BtVBVgwhwwEeRpCewwCeg0EewSAeQ4BeRpBeQaCeQ4DeQpB?eglBexSCewhhlDeAPAewhAewhAewSAeRaBeSpBtwhgWh0A8?Je5RBvhB9SBcHBmfAtHeBtFewhwwAtwhAewwAeRpAewhxww?Sxwg0RpAewhwwgWwSQ4wwi0BeQagWAeQ4glg0RpAeQpQawS?whhlwSAeQpAeQpAeglQpAeglxSAPAeAtwhRpwSAeCPAeBth?0xhwSwwglA8APQpBtxhxwg0A8QLRpBtwhi0A8AAEeA8MeKy?AvhLP6ApUBzBB6UBvOBTKBGEBFIBZNB2VB0LBuRB3fBtAeh?0Eeg0Btg0whEej0whi0BewSwhglQLwhwwi0AeglBtglQ4A8?AeC8gHilQ4ilB8QaQpg0Q4whQpilA8AAEeA8CeA8EeAACeA?8EeAACeA8EeAAMe9KBvhJMKBSOBPHBzCBOOBpIBZVBMeBTd?BibBNgilwhRpAeAtAeQ4glRpwhRpBtF8AeI8AeE8i0Q4wSw?wA8AtA8whg0xwQ4wSwwBtneNGBvhAXEB9fR4BewwDeQ4Aew?DCewwilgHAPQpAeAtRpi0Q4wSwwA8At8eAADeA8Mev8AvhT?yCBFFBTFBUCBGDBJiBMdB+eBzYB1gBvcBKaBJiBtpB0rBfq?BOkBznB6oBpUBUgwhdeh0FeglwhAewhEeQpwhCeQ4AeBtDe?APgWBeQpAeAtBeAtg0QLwhA8whBtxwglJeTQBvhEJdB6lB+?mBtnBXdBegwhFeR4AewhRpCeR4wwBeRpCeg0gHgWwwAeRpC?eQagHAeglwhwwwSC8glQ4RpQ4hlAeB8glQ4Qph0AeA8AAQe?ZGBvhG2gB0bBfcBaeBTUBscBFXB3fwhGeRpwhBeywAtAeRp?whBeQ4wwBthlxhBeR4AtR4glwhQ4AeF8xwQ4A8AeSpAtA8x?wQ4A8AewhQpBth0R4B8xhAtwhwDg0Q4AeA8EeAADeA8DeAA?DeA8DeAALe5GBvhQvEBT+AK3AWABFCBcHBUIBFJB6GBJRBT?GB+ZBcWBfZB2cBTZBiWBfgRpilCeRpCeglAtBeQ4Bexwg0A?tglB8wDxwieAAAeA8LeFTBvhGvSB5iBKkB+oBUlBTnBfoBw?gQ4BeglFeR4g0glBtAeQ4AeQpAPQ4h0D8AeA8xhglg0APAt?A8whA8xwwhglAeAACeA8NeZLBvhMlbBCWBTUBOYBdSBpcBH?gBshBSoB1gBeeBTYBpUBsfwhIewhFeRpg0whCewwBeRpg0x?hAeywAeAth0glwhAeR4wwBthlwhA8AeG8Q4A8AeD8xwglQ4?C8QpB8wSwwglR4A8SpA8APhlg0Q4A8xhQpAtAPi0AeA8DeA?ADeA8DeAAMevDBvhGs5A5LB3MB1VBzOBiQB2VBVgg0Jeh0C?eAtRpwhilC8APxwQ47eSSBvhG0LBXNBUHBJnBTjBuiB9ZB2?fwwHexwAeBtDeh0wwDtR4Aeg0Rpg0BtR4glAeg0RpF8AeB8?QpF8AeA8RpA8APAtD8hlQpAtAPBtxhA8glxwglAPAtxhg0A?8glxwAeAADeA8DeAADeA8MeWDBvhBX4AT5A9eR4RpCewwAe?R4g0RpBexwAeBti0Aeh0wwDtR4Aeg0Rpg0BtR4glAeg0RpB?8xhxwAeB8QpA8xhglxwAeA8RpA8APAtilA8hlQpAtAPBtxh?A8glxwglAPAtxhg0A8glxwAeAADeA8DeAADeA8DeA8EeAAC?eA8EeAACeA8EeAACeA8EeAACeA8EeAALeJ1AvhUM6AaxAO3?AF6A67A04Al8A5LBXTBTQBTGBKMBNSB+UB5nB0mBTaBPiBt?nB5gBOlBNgh0FeRpg0whAeQ4DeRpg0whAeR4wwAeg0G8AeB?8AeK8hlE8AexwglQ4A8whC8AexwglQ4A8xhQpA8gHGeA8Ae?AAJeKBBjfglIeglJeglh0FewhQpg0whAeQ4DexhAeQLAeR4?wwAeg0xwglQ4A8whA8AeB8wSwwglQ4A8xhQpA8glGeA8AeA?AA8HeAAYeAACeA8EeAACeA8JeMFBvhO3/AODBPEBtJBzGBq?HBMDBMEBT8Ai9A9KBpABJFBPJBuJBgfh0whEeQ4Aeg0xhEe?R4g0xhAeP8hlQaE8whA8glQ4QaE8xhglQ4QaA8HeAAEeAAC?eA8EeAACeA8GeAAAeA8GeAAAeA8EeA8AeAAGeA8AeAALedw?AvhbzzAc3AZtAO4AC4A/tAM5ATzAv6AN0ApKB+PB0BBfPBT?PBuXBlWB6iBpcBKfBylB3bBTjB+ZBJdBsaB5nB3mB4gR4Be?g0BeAtAeR4whBeg0glBtR4hHAeg0AeB8AtA8wDwhQ4B8glg?0BtwhwDR4A8hlJelbBvhATaBfgRpywR4Beg0RpAtwwR4whB?eg0QawhQahlhHAeg0AexwAtQpwDwhQ4B8glg0BtwhwDR4A8?hlAACeA8EeAACeA8OetYBvhKMZBOhBikBCmB+nBNoBZkBzp?B0lBPsBUsBugBtCewhAeBtAeQ4BtwwAeD8AeI8AeI8AeBtC?8Q4A8BtAewhBtQpA8JeCWBggilAeBtCewhAehlAeQpAeAtw?wAeQ4g0BtAewhBtQpA8heA8AANeNLBBgwwJewwIewShlAeB?tCeAPAehlAeQpAeAtwwAeQ4g0BtAewhBtQpA8heA8AAXeGP?B3fwwJewwIewShlAeBti0APAehlAeQpAegWQaAeQ4g0BtAe?whBtQpglheA8AASeA8AAMeTABvhBZXB/LBtfQ4IeR4HewwQ?4FeRpgWwwFeRpgWQLhlAeBti0APQLBtAeQ4BtglSpC8AeB8?xwQpi0A8APAtilQ4g0BtA8whAPAtQpglneTEBvhRONBpAB3?MBiMB0BBpSBidB/bB1iBTQBOZBMYB9hBFhBvmBTlBasB0sB?5gBtDeRpQ4hlBtAeI8AeF8BtA8AeB8xwwhh0BtKemYBvhGp?oBWrBpjBKpBzpBUrBXqBrgR4EewhAeR4BtDewhG8AeI8AeD?8xhC8AeA8Q4A8xhBtB8AeA8Q4JeNVBVgwwJewwR4EewhAeg?lAewSAtEeQpxhBtB8AeA8Q4jeAAA8Le5iBvhGznBvmBSjB0?aBmVB8bBXZBBgi0AeAtCeR4Btg0BtBeR4glwwCtQ4Beilxw?T4AeRpwhQaxhAtAeQ4AeQpAPgHBtglBtA8Aexhg0QpCtwhB?8h0gWRpzhA8xwQaQpxhBtwhA8xwQaFeA8BeAAJeT3AvhKJT?BtXBKYBlWBpoBGrBOiB6tBMlB3fBThBlgh0CeR4AtRpg0Ce?R4BtRpAegHhlQpAPwSglRpAPBeglBPxhglQpAPAegWAexwA?Pilg0B8g0xwQLRpglg0B8g0JetiBvhAcaBggAtHeAtEeh0C?eQpQ4AtRpAegHBeQaAPgWwhglQpAPwwAeglxwQLRpglg0Qp?A8g0LeA8DeAALeFeBvhE+eBJgBSrBXmBTgBrgRpHeRpR4Ce?g0AeAtgWAtQ4glCeg0B8xwxhA8AeA8glA8AtQ4whwDg0C8g?lDeAABeA8LePYBvhDTMBcaBZLBuLBBgwhh0IewSQpKeQ4Ee?gWDeQ4CeQpCeQ4Deg0BegWAtAeQLwDCeBtQ4xhg0A8AeA8g?lDeAABeA8feldBvhJMeB6dBZmB2fBirBTiBFtB0fB3mB5lB?zgBtAewhFeglAeAtBeR4DehlAeQ4AeAPQpBeQLCexDDeQ4h?lQ4B8xwAeA8JePWBvhBNtBziB4fQ4IeR4GeBtQ4whCeRpAe?g0BtwhAeR4RpAei0whR4RpwwAewhQag0whBeRpxwA8xhAeF?8BtwhQaC8xwA8glBtQaA8xhxwA8ilQaxhxwQpA8Q4hlQ4A8?AexwRpJeO8Ajfh0IewhJeQ4GegWwSAewhCeRpAeglAewSBe?R4DehlAeQ4AeAPAeglAeQLCexDCewwQ4hlQaB8xwRpgeA8A?eAAFeAABeA8NeNDBvhWi/As5ApFB/HBzJB6KBaNBuNB5bB2?kB0gBveBtiBzbB/fBlmBMnBpeBaqBmiBzfBOfB6gBLgh0hl?Eewhg0RpglBei0whg0RpglCeAtg0whQ4hlRpAeBtQ4yhAew?hQpAeAtwwQ4g0glwSwwg0B8ilQ4glwSwwg0C8AtglQ4whgW?g0xwA8BtwhQ4whwDg0xwA8AtQpxhJelSBvhLMPBvXBTYB9X?B5kBTpBKqBUsBOrB3kB5lBTiBigwhBeRpCeR4whh0RpBeR4?glwhg0BtCeRpglAeg0wwxhAeAtA8xhQ4gHglxwB8xhg0Q4g?HBtC8xwg0Q4gHQpBtA8AtJe3NBofR4FewhR4RpCeR4whh0R?pBeR4glwhg0BtCeRpglwhg0wwBtAeAtD8AexhF8Q4wDwhxw?C8xhQ4gHglxwB8xhgWQ4glBtC8xwgWQ4glQpBtA8AtBeAAA?eA8GeAAAeA8GeAAAeA8Oei3AvhEFFB28A04AsyAZoATewhI?ewhIewhAeAtGewhBtGeg0CtFewwh0BtR4CewhhlwhR4RpBe?whQ4Qpwhh0RpBeQ4BtA8AeE8glCtAeE8ilAtAPxhC8h0gWQ?4xhxwB8g0whwDQ4hlxwB8BeAAAeA8GeAAAeA8GeAAAeA8Ge?A8CeAAEeA8CeAAEeA8CeAAEeA8CeAAEeA8CeAAMe/hAvhFK?sApnAdsAO0AM5AT1AnewhIewhEeRpBewhAeAtBeAtRpwwAe?whBtAeBtQ4xwwhB8AeG8Q4A8AeG8Q4A8AeC8xwB8Q4A8APB?8AtxwQpA8Q4AtAPA8BtwhRpQ4BeA8CeAAEeA8CeAAEeA8Ce?AAEeA8CeAAEeA8CeAAHeAAA8HeAAA8HeAAA8HeAAA8HeAAA?8MezaAvhCpYAtZAfdAkeQ4AexhwwFeQ4CewwRpAeQpAPCeR?awSwhBtglgWAeQ4AtAPA8BtwhRpQ4peA8CeAAveAAA8teAA?AeA8Ne6WAvhIlaAOcAMdAJuATtA3uAOzAKtAMxAneglHeAt?glGeBthlh0DeBtRpg0R4Aeh0Btg0E8AeB8Atg0E8AeA8Bth?0hlB8AeA8BtxwglxhA8gHglBtOeAAA8HeAAA8HeAAA8HeAA?A8PeAAAeA8keA8AeAANeChAvhLFgAOrAXpAJ1Az6ATzApNB?GKB9LBvSB0SBiQBLgilGeglwwRpBtAel0AeF8g0QpxwAtAP?A8ilCeA8AeAA1eiJBvhAGKBqfi0ilCeilg0C8AeK8AeI8Ae?A8ili0B8Aei0glCeA8AeAAJf30AvhWTtAlzA84ApmBSUBpm?BXeBOiBTaBNgB0WBpmBvnBToBKkBWlBlrBUlBbaB+oBnmB8?nBqlBVgRpAeglFeRpAeglDeAtg0AeBthlR4Btg0gli0BeR4?RpxwA8g0AeE8xwA8g0AeC8AtglA8Btg0gWxhBtglg0ilAeA?8xhxwJeZHBvhESTBNNBdOBUPBGPBlfwhCewwi0BewhAeywB?tg0AeBthlR4Btg0gli0BeR4RpD8AeG8Q4A8AeA8QpilB8Q4?A8QLRpBtglA8Btg0gWxhBtglg0ilB8xhwSwwDeA8CeAAEeA?8CeAAEeA8CeAAEeA8CeAAKe3vAvhDTsAN3A5zAG1A0ewhAe?i0RpCewhAeR4g0RpwwAexhR4wwi0gli0BeR4RpD8AeH8Q4A?eilxwC8Q4AexhglxwQpA8R4wDwhQpilg0ilB8xhwSwwDeA8?CeAAEeA8CeAAEeA8CeAAEeA8CeAAFeAABeA8FeAABeA8FeA?ABeA8FeAABeA8FeAABeA8KeSYAvhK/eAZ0A0tATjAXnAa0A?OzAz0AZ0At5AU4AQfBtAewhwwDeRpBtwhxwAeR4H8AeB8Bt?A8Q4QpB8AeA8xwBtQ4RpA8wDwhYeAABeA8FeAABeA8FeAAB?eA8UeAADeA8DeAADeA8DeAADeA8NeijAvhO8kA2eA9rAJAB?z5ANFBvFBvEBCDBzBBc8AGFBpXBGcBTbBggAtRpFeBtRpGe?AtxwF8APAtxwF8JeA8AAHeA8AAReSZBvhAMPBKgAtHeAtBe?AtRpEegWAtFeglgWAeAPAtxwC8i0TeA8AASeA8GeAAJe1SB?vhGfNBZVB+dB0cBFZB5fBalBWghlDeywAewhglQ4BtAeg0w?wAtAewhglR4Btg0BtI8AeA8h0C8AeSpA8Q4g0whBtAeglQp?AtA8Q4g0xhAtAPglBtFeAABeA8JePOBvhH0HBZLBTTBmRBT?JB/QB9NBVMBDgwwBewwRpBeQ4wwAexwFeglyhEegHAehWAe?glDeQ4whBtglwhxwA8GeAABeA8jeA8BeAAJeK8AvhE09AvU?BTPBpPBKSB7fglDewhglCeglDewhglAeBthlCewhD8g0A8A?eB8Q4g0C8g0A8AeB8Q4g0A8Bth0AeB8Q4PeA8BeAAFeA8Be?AAFeA8BeAATeJTBvhI2dBtVB/WBzYB6XBcVB+NBuRBcLB4f?AtCeg0DeBtAei0DeAtBeh0GeQ4AeBPEewhQ4whBPAeRpwhB?twhglh0C8AeAtQpxhh0A8xwQaTeAAHeA8AAHeA8JeTKBvhE?NOBvOBJHBNGBXIB3fwwJewwwhAeR4EeQaAeQ4AeQaQ4CeQa?BeRaBeQ4BeBtQ4ilxhA8eeAAHeA8JeA8AARe89AvhM6UBGZ?BpZByiB3dBscBzaBTZBGbBKXB9YBWWBTPBTgRpAeg0BeglB?ewwDeh0BewwHeglQaDexSAehlAeglAtgWAexwglCtxhQ4Je?A8AAIeA8BeAAOesNBvhCZaBCZBAAA";

//        String command = "util seq -t v115@vhFRQJUGJKJJvMJTNJGBJ v115@vhFRQJPGJKJJGMJTNJ0BJ -p *!";
//        String command = "util seq -t v115@vhFRQJUGJKJJvMJTNJGBJ#:-1 v115@vhFRQJPGJKJJGMJTNJ0BJ#1:5 -p *!";
//        String command = "util seq -t v115@vhERQJPGJKJJGMJTNJ -p *!";

//        String command = "util fumen -M reduce -t v115@vhdXKJNJJ0/ISSJzHJGDJJHJSGJvLJ0KJJEJzJJ+NJ?tMJFDJz/IyQJsGJOGJpFJ+NJ3MJXDJULJzGJxBJiJJtKJyJ?J0JJ";

//        String command = "util seq -M pass -hh no -p *! -e ^.{0,1}?S";

        String command = "verify kicks";
        int returnCode = EntryPointMain.main(command.split(" "));
        System.exit(returnCode);

//        ExecutorService executorService = Executors.newFixedThreadPool(6);
//        try (AsyncBufferedFileWriter writer = new AsyncBufferedFileWriter(new File("output/test"), Charset.defaultCharset(), false, 10L)) {
//            for (int i = 0; i < 10; i++) {
//                int finalI = i;
//                executorService.submit(() -> {
//                    for (int j = 0; j < 10; j++) {
//                        writer.writeAndNewLine(String.format("hello %d-%03d", finalI, j));
//                    }
//                });
//            }
//            executorService.shutdown();
//            executorService.awaitTermination(100L, TimeUnit.SECONDS);
//        }
//        LoadedPatternGenerator generator = new LoadedPatternGenerator("TISZ*![tisz]");
//        int depth = generator.getDepth();
//        System.out.println(depth);
//        generator.blocksStream().map(Pieces::getPieces).forEach(System.out::println);

//        HTMLBuilder<TestColumn> builder = new HTMLBuilder<>("hello");
//        builder.addColumn(TestColumn.SECTION1, "hello");
//        builder.addColumn(TestColumn.SECTION2, "test1");
//        builder.addColumn(TestColumn.SECTION1, "world");
//        builder.addColumn(TestColumn.SECTION2, "test2");
//        List<String> list = builder.toList(Arrays.asList(TestColumn.SECTION1, TestColumn.SECTION2), true);
//
//        StringBuilder builder1 = new StringBuilder();
//        String lineSeparator = System.lineSeparator();
//        for (String s : list) {
//            builder1.append(s).append(lineSeparator);
//        }
//        System.out.println(builder1.toString());

    }

    public enum TestColumn implements HTMLColumn {
        SECTION1,
        SECTION2;

        @Override
        public String getTitle() {
            return name();
        }

        @Override
        public String getId() {
            return name();
        }

        @Override
        public Optional<String> getDescription() {
            return Optional.empty();
        }
    }
}
