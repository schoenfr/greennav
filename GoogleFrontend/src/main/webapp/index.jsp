<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />

        <title>GreenNavigation</title>

        <link rel="stylesheet" type="text/css" href="css/style.css" />

        <script type="text/javascript"
        src="http://maps.google.com/maps/api/js?sensor=false"></script>

        <script type="text/javascript" src="js/jquery-1.11.1.min.js"></script>
        <script type="text/javascript" src="js/json2.js"></script>
        <script type="text/javascript" src="js/GreenNavInit.js"></script>
        <script type="text/javascript" src="js/GreenNavRange.js"></script>
        <script type="text/javascript" src="js/GreenNavRoute.js"></script>
        <script type="text/javascript" src="js/GreenNavClick.js"></script>
    </head>

    <body>
        <div id="header">
            <div id="header_logo">
                <a href="./"><img src="images/logo.jpg" height="80"
                                  alt="Green Navigation" /></a>
            </div>
            <div id="header_nav">
                <a href="javascript:;" id="info">Info</a> | <a href="javascript:;"
                                                               id="contact">Kontakt</a>
            </div>
        </div>

        <div id="data_input">
            <div id="t_input">
                <a id="toggle_input" href="javascript:;"><img
                        src="images/open.png" height="10" alt="open/close" /></a>
            </div>

            <div id="input">
                <form>
                    <div id="t_route_range">
                        <a id="show_route" href="javascript:;">Route</a> <a id="show_range"
                                                                            href="javascript:;">Erreichbarkeit</a>
                    </div>

                    <!-- route definition - start -->
                    <div id="route_input">
                        <div id="route_start">
                            <label>Start:</label> <input type="text" value=""
                                                         class="inputfield if_long" />
                        </div>

                        <div id="steps"></div>

                        <div id="route_dest">
                            <label>Ziel:</label> <input type="text" value=""
                                                        class="inputfield if_long" />
                        </div>

                        <a id="add_step" href="javascript:;">Zwischenstop einf&uuml;gen</a>

                        <br />

                        <div id="options">
                            Fahrzeug: <select id="route_vehicle_type" size="1">
                                <option>Smart Roadster</option>
                            </select> <br /> <br /> Optimierung: <select id="route_optimization"
                                                                         size="1">
                                <option>ENERGY</option>
                                <option>DISTANCE</option>
                                <option>TIME</option>
                            </select> <br /> <br /> Energiestand: <input id="route_energy"
                                                                         type="text" size="6" class="inputfield" /> %
                        </div>  

                        <label class="checkbox-label">
                            <input type="checkbox" id="chargingStations" value="Aufladestationen verwenden"/>
                            <span>Aufladestationen verwenden</span>
                        </label>

                        <br /><br />
                            <input id="route" type="button" value="Route berechnen"
                                   class="button" />
                            <div id="route_details"></div>
                    </div>
                    <!-- route definition - end -->

                    <!-- range definition - start -->
                    <div id="range_input" style="display: none">
                        <div id="range_address">
                            <label>Start:</label> <input type="text" value=""
                                                         class="inputfield if_long" />
                        </div>

                        <div id="options">
                            Fahrzeug: <select id="range_vehicle_type" size="1">
                                <option>Karabag Fiat 500E</option>
                                <option>Sam</option>
                                <option>STROMOS</option>
                                <option>Luis</option>
                                <option>SmartRoadster</option>
                                <option>MUTE</option>
                            </select> <br /> <br /> Energiestand: <input id="range_energy"
                                                                         type="text" size="6" class="inputfield" /> %
                        </div>
                        <input id="range" type="button" value="Reichweite berechnen"
                               class="button" />
                    </div>
                    <!-- range definition - end -->
                </form>
            </div>

            <br />
            <div id="loading">loading...</div>
            <div id="directions"></div>
        </div>

        <div id="map"></div>
        <div id="bg"></div>

        <div id="info_greennav" class="dialog">
            <a href="javascript:;" class="close_dialog">Close</a> <b>Green
                Navigation</b><br /> Diese Webanwendung entsteht im Rahmen der Forschung
            zur gr&uuml;nen Navigation am Institut f&uuml;r Softwaretechik und
            Programmiersprachen (ISP) an der Universit&auml;t zu L&uuml;beck. Im Vergleich
            zu anderen Routenplanern ist das Ziel von Green Navigation einen
            m&ouml;glichst energieeffizienten Weg zu berechnen. Desweiteren ist es
            m&ouml;glich, die Reichweite von Elektrofahrzeugen abh&auml;ngig von ihrem
            Ladezustand zu berechnen. <br /> Im Rahmen dieser Forschung waren
            verschiedene studentische Projektarbeiten beteiligt. Weitere
            Informationen finden Sie auf unserer Homepage. <br /> <br /> <b>H&auml;ufig
                gestellte Fragen</b><br />
            <ul>
                <li>Warum funktioniert die Strecke von A nach B nicht?</li>
            </ul>
            Hierf&uuml;r kann es verschiedene Gr&uuml;nde geben. Da Green Navigation derzeit
            nur das Gebiet Schleswig-Holstein umfasst, k&ouml;nnen Start- und Zielpunkt
            auch nur aus Schleswig Holstein gew&auml;hlt werden. Wir arbeiten bereits
            an der Integration deutschlandweiter Daten. Eine weitere M&ouml;glichkeit
            w&auml;re, dass der Routing-Server derzeit nicht verf&uuml;gbar ist
            (beispielsweise aufgrund von Wartungsarbeiten).
            <ul>
                <li>Wo finde ich weitere Informationen zur Funktionsweise von
                    GreenNav?</li>
            </ul>
            Wir verweise dabei auf die unten angegebenen Links. Hier finden Sie
            eine etwas detailliertere Beschreibung von GreenNav. <br /> <br /> <b>Links</b><br />
            <ul>
                <li><a
                        href="https://www.isp.uni-luebeck.de/research/projects/green-navigation"
                        target="_blank">Homepage des ISP</a></li>
                <li><a href="http://redmine.isp.uni-luebeck.de/" target="_blank">Redmine
                        f&uuml;r Entwickler</a></li>
            </ul>
        </div>

        <div id="contact_greennav" class="dialog">
            <a href="javascript:;" class="close_dialog">Close</a> <b>Green
                Navigation - Kontakt</b><br /> <img
                src="https://www.isp.uni-luebeck.de/sites/default/files/pictures/18416%20H.%20Sch%C3%B6nfelder%20Set%203%20%281%29Cd.Jpg"
                style="height: 150px; float: left; margin: 5px 5px 5px 5px;" />
            <table>
                <tr>
                    <td>Name</td>
                    <td>Ren&eacute; Sch&ouml;nfelder</td>
                </tr>
                <tr>
                    <td>Website:&nbsp;</td>
                    <td><a href="https://www.isp.uni-luebeck.de/staff/schoenfr">https://www.isp.uni-luebeck.de/staff/schoenfr</a></td>
                </tr>
                <tr>
                    <td style="vertical-align: top;">Adresse:&nbsp;</td>
                    <td>Institut f&uuml;r Softwaretechnik und Programmiersprachen
                        Universit&auml;t zu L&uuml;beck <br /> Ratzeburger Allee 160 <br /> 23562
                        L&uuml;beck <br /> Deutschland
                    </td>
                </tr>
                <tr>
                    <td>Telefon:&nbsp;</td>
                    <td>+49 451 500 5688</td>
                </tr>
                <tr>
                    <td>B&uuml;ro:&nbsp;</td>
                    <td>Haus 64, Raum 83 (Erdgeschoss)</td>
                </tr>
            </table>

            <br clear="left" /> <b>Haftung</b><br /> Diese Seite ist ein Teil
            der Internetpr&auml;senz des Instituts f&uuml;r Softwaretechnik und
            Programmiersprachen an der Universit&auml;t zu L&uuml;beck. Weitere
            Informationen finden Sie hier: <a
                href="https://www.isp.uni-luebeck.de/impressum.html">https://www.isp.uni-luebeck.de/impressum.html</a>.
            <br /> <br /> Trotz sorgf&auml;ltiger inhaltlicher Kontrolle
            &uuml;bernehmen wir keine Haftung f&uuml;r Inhalte externer Links.
            F&uuml;r den Inhalt der verlinkten Seiten sind ausschlie&szlig;lich
            die jeweiligen Betreiber verantwortlich.
        </div>
        <div id="error"></div>
        <div id="log"></div>
    </body>
</html>
