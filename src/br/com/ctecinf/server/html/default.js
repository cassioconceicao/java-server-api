/* 
 * Copyright (C) 2021 ctecinf.com.br
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

Number.prototype.format = function () {

    var str = this.toString();

    if (str === 'NaN') {
        return "";
    }

    if (str.length === 1) {
        str += ".00";
    } else if (str.length > 1) {
        var len = str.length - 2;
        str = str.substring(0, len) + "." + str.substring(len);
    }

    var num = parseFloat(str);

    return num.toFixed(2).replace('.', ',').replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1.');
};

/**
 * Chamada AJAX com resposta arquivo JSON
 * 
 * @param {string} url
 * @param {string} params [param1=value1&param2=value2...]
 * @param {function} callbackFunction Função para executar na resposta [function(response, message){ ...implementar método }]
 * @param {string} method POST|GET DEFAULT "POST" [OPCIONAL]
 */
function ajax(url, params, callbackFunction, method) {

    if (typeof method === 'undefined') {
        method = "POST";
    }

    var ajax = typeof XMLHttpRequest !== 'undefined' ? new XMLHttpRequest() : new ActiveXObject('Microsoft.XMLHTTP');
    ajax.open(method, url);
    ajax.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    ajax.responseType = 'json';

    ajax.onreadystatechange = function () {

        if (this.readyState === 4 && this.status === 200) {

            var message = null;

            if (typeof ajax.response.message !== 'undefined') {
                message = ajax.response.message;
            }

            callbackFunction(ajax.response, message);
        }
    };

    ajax.send(params);
}

/**
 * Funções para ordenação, paginação e filtro de tabela
 * *****************************************************************************
 */

/**
 * Ordena tabela pela coluna
 * 
 * @param {string} tableId Identificador da tabela
 * @param {int} columnIndex Índice da coluna
 */
function sortTable(tableId, columnIndex) {

    var spinner = document.getElementById("spinner");

    if (spinner !== null) {
        spinner.scrollIntoView();
        spinner.style.display = "";
    }

    setTimeout(function () {

        var tBody = document.getElementById(tableId).getElementsByTagName("TBODY")[0];
        var rows = tBody.getElementsByTagName("TR");
        var data = [];

        for (var i = 0; i < rows.length; i++) {
            var row = [];
            for (var j = 0; j < rows[i].getElementsByTagName("TD").length; j++) {
                row[j] = rows[i].getElementsByTagName("TD")[j];
            }
            data.push(row);
        }

        data = data.sort(function (a, b) {
            if (a[columnIndex].innerHTML > b[columnIndex].innerHTML) {
                return 1;
            } else if (a[columnIndex].innerHTML < b[columnIndex].innerHTML) {
                return -1;
            } else {
                return 0;
            }
        });

        tBody.innerHTML = "";
        data.forEach(function (item) {
            var tr = document.createElement("TR");
            item.forEach(function (cell) {
                tr.appendChild(cell);
            });
            tBody.appendChild(tr);
        });

        if (spinner !== null) {
            spinner.scrollIntoView();
            spinner.style.display = "none";
        }
    }, 50);
}

/**
 * Filtro para tabela
 * 
 * @param {type} tableId Identificador da tabela
 * @param {string} filterValue Valor para pesquisa na tabela
 */
function filterTable(tableId, filterValue) {

    var rows = document.getElementById(tableId).getElementsByTagName("TBODY")[0].getElementsByTagName("TR");

    for (var i = 0; i < rows.length; i++) {

        rows[i].style.display = "none";
        var td = rows[i].getElementsByTagName("TD");

        for (var j = 0; j < td.length; j++) {

            var cell = rows[i].getElementsByTagName("TD")[j];

            if (cell && cell.innerHTML.toUpperCase().indexOf(filterValue.toUpperCase()) > -1) {
                rows[i].style.display = "";
                break;
            }
        }
    }
}

/**
 * Inicia tabela
 * 
 * @param {string} tableId Identificador da tabela
 * @param {int} limit Número máximo de registro por página
 * @param {string} url
 * @param {string} filterId Identificador o campo de filtro da tabela [OPCIONAL]
 * @param {string} formURL URL para direcionar ao clicar sobre a linha da tabela passando como parâmetro <i>ID</i> do registro.[OPCIONAL]
 */
function initTable(tableId, limit, url, filterId, formURL) {

    ajax(url, 'table=' + tableId + '&action=count', function (response, message) {

        if (message !== null) {
            alert(message);
        } else {

            var pages = Math.ceil(response.total / limit);

            if (pages > 1) {

                var div = document.getElementsByClassName("ctecinf-pagination");

                for (var i = 0; i < div.length; i++) {

                    var link = document.createElement("a");
                    link.href = "javascript:loadPageTable('" + tableId + "', 1, " + limit + ", '" + url + "', '" + filterId + "'" + (typeof formURL !== 'undefined' && formURL.length > 0 ? ", '" + formURL + "'" : "") + ")";
                    link.innerHTML = "&laquo";
                    div[i].appendChild(link);

                    for (var j = 1; j < pages + 1; j++) {

                        link = document.createElement("a");
                        link.href = "javascript:loadPageTable('" + tableId + "', " + j + ", " + limit + ", '" + url + "', '" + filterId + "'" + (typeof formURL !== 'undefined' && formURL.length > 0 ? ", '" + formURL + "'" : "") + ")";
                        link.innerHTML = j;
                        if (j === 1) {
                            link.className = "active";
                        }
                        div[i].appendChild(link);
                    }

                    link = document.createElement("a");
                    link.href = "javascript:loadPageTable('" + tableId + "', " + pages + ", " + limit + ", '" + url + "', '" + filterId + "'" + (typeof formURL !== 'undefined' && formURL.length > 0 ? ", '" + formURL + "'" : "") + ")";
                    link.innerHTML = "&raquo;";
                    div[i].appendChild(link);
                }
            }

            loadPageTable(tableId, 1, limit, url, filterId, formURL);
        }
    });
}

/**
 * Atualiza página da tabela via AJAX
 * 
 * @param {string} tableId Identificador da tabela
 * @param {int} page Página para selecionar
 * @param {int} limit Número máximo de registros por página
 * @param {string} url URL para consulta via ajax. Parâmetros do <i>send</i> [action=find&offset=?&limit=?]
 * @param {string} filterId Identificador do campo de filtro da tabela [OPCIONAL]
 * @param {string} formURL URL para direcionar ao clicar sobre a linha da tabela passando como parâmetro <i>ID</i> do registro.[OPCIONAL]
 */
function loadPageTable(tableId, page, limit, url, filterId, formURL) {

    var spinner = document.getElementById("spinner");

    if (spinner !== null) {
        spinner.style.display = "";
        spinner.scrollIntoView();
    }

    var offset = page === 1 ? 0 : (page - 1) * limit;

    ajax(url, 'table=' + tableId + '&action=find&offset=' + offset + '&limit=' + limit, function (response, message) {

        if (message !== null) {
            alert(message);
        } else {
            var rows = "";
            response.forEach(function (item) {
                rows += "<tr><td><input class=\"ctecinf-table-checkbox\" type=\"checkbox\" id=\"selecteds_id\" name=\"selecteds_id[]\" value=\"" + item.value + "\" /></td><td" + (typeof formURL !== 'undefined' ? " onclick=\"window.location='" + formURL + "?id=" + item.value + "'\"" : " onclick=\"window.location='form.php?id=" + item.value + "'\"") + ">" + item.value + "</td><td" + (typeof formURL !== 'undefined' ? " onclick=\"window.location='" + formURL + "?id=" + item.value + "'\"" : " onclick=\"window.location='form.php?id=" + item.value + "'\"") + ">" + item.label + "</td></tr>";
            });

            document.getElementById(tableId).getElementsByTagName("TBODY")[0].innerHTML = rows;

            var div = document.getElementsByClassName("ctecinf-pagination");

            for (var i = 0; i < div.length; i++) {
                var href = div[i].getElementsByTagName("a");
                for (var j = 0; j < href.length; j++) {
                    href[j].className = "";
                    if (j === page) {
                        href[j].className = "ctecinf-pagination-active";
                    }
                }
            }

            var filter = document.getElementById(filterId);

            if (filter !== null) {
                filterTable(tableId, filter.value);
                filter.focus();
            }

            if (spinner !== null) {
                spinner.style.display = "none";
            }
        }
    });
}

/**
 * Marca ou desmarca todos checkbox com ID da tabela
 * 
 * @param {element} source
 */
function toggleTable(source) {
    var checkboxes = document.getElementsByClassName("ctecinf-table-checkbox");
    for (var i = 0; i < checkboxes.length; i++) {
        checkboxes[i].checked = source.checked;
    }
}

/**
 * AJAX com arrays dos [id] selecionados no checkbox da table
 * 
 * @param {string} tableId Tabela
 * @param {string} action Ação no controller, "delete", ...
 * @param {string} url URL do controller
 */
function actionTable(tableId, action, url) {

    if (action.length > 0) {

        var checkboxes = document.getElementsByClassName("ctecinf-table-checkbox");
        var params = [];

        for (var i = 0; i < checkboxes.length; i++) {
            if (checkboxes[i].checked) {
                params[params.length] = "id[]=" + checkboxes[i].value;
                checkboxes[i].checked = false;
            }
        }

        if (params.length === 0) {
            alert("Nenhum registro selecionado.");
        } else {
            ajax(url, "table=" + tableId + "&action=" + action + "&" + params.join("&"), function (response, message) {
                alert(message);
            });
        }
    }
}

/**
 * Funções do Menu
 * *****************************************************************************
 */
/**
 * Abre Menu lateral
 */
function openMenu() {
    document.getElementById("menu").style.width = "300px";
}

/**
 * Fecha Menu lateral
 */
function closeMenu() {
    document.getElementById("menu").style.width = "0px";
}

/**
 * Funções do campo <i>autocomplete</i>
 */
/**
 * AutoComplete "onkeyup='autoComplete(params...)'"
 * 
 * @param {string} searchId Identificador do INPUT Label do campo.
 * @param {string} fieldId Identificador do INPUT Value do campo ID da coluna, tipo hidden.
 * @param {string} targetId Identificador da DIV onde vai a lista de resultados para selecionar.
 * @param {string} url URL para consulta AJAX.
 * @param {string} table Nome da tabela no banco de dados.
 */
function autoComplete(searchId, fieldId, targetId, url, table) {

    var search = document.getElementById(searchId);
    var target = document.getElementById(targetId);

    ajax(url, 'table=' + table + '&action=find&term=' + search.value + '&limit=' + 8, function (response, message) {

        if (message !== null) {
            target.innerHTML = "";
            alert(message);
        } else if (response.length > 0) {

            var list = "";

            response.forEach(function (item) {
                list += "<a href=\"javascript:setSelectedAutoComplete('" + fieldId + "','" + searchId + "','" + item.value + "', '" + item.label + "', '" + targetId + "')\">" + item.label + "</a>";
            });

            target.innerHTML = list;
            target.style.border = "1px solid $borderColor";

        } else {
            target.innerHTML = "";
            target.style.border = "none";
        }
    });
}
/**
 * Seleção do AutoComplete
 * 
 * @param {string} valueId
 * @param {string} labelId
 * @param {string} value
 * @param {string} label
 * @param {string} targetId
 */
function setSelectedAutoComplete(valueId, labelId, value, label, targetId) {
    document.getElementById(valueId).value = value;
    document.getElementById(labelId).value = label;
    document.getElementById(targetId).innerHTML = "";
    document.getElementById(targetId).style.border = "none";
}


/**
 * Funções de formulário
 * *****************************************************************************
 */
/**
 * Inicia valores do formulário
 * 
 * @param {string} formId
 */
function initForm(formId) {

    var spinner = document.getElementById("spinner");

    if (spinner !== null) {
        spinner.style.display = "";
        spinner.scrollIntoView();
    }

    var form = document.getElementById(formId);

    if (document.getElementById("id").value > 0) {

        ajax(form.action, 'action=find&id=' + document.getElementById("id").value, function (response, message) {

            if (message !== null) {
                alert(message);
            } else {

                for (var i = 0; i < form.elements.length; i++) {
                    if (form.elements[i].name.indexOf('data') !== -1) {
                        form.elements[i].value = response.data[form.elements[i].id];
                    }
                }

                if (spinner !== null) {
                    spinner.style.display = "none";
                }
            }
        });

    } else {

        if (spinner !== null) {
            spinner.style.display = "none";
        }
    }
}

/**
 * AJAX com data[colunas] do formulário
 * 
 * @param {string} tableId Tabela
 * @param {string} action Ação no controller, "delete", "save", ...
 * @param {string} url URL do controller
 */
function actionForm(tableId, action, url) {

    if (action.length > 0) {

        var checkboxes = document.getElementsByClassName("ctecinf-table-checkbox");
        var params = [];

        for (var i = 0; i < checkboxes.length; i++) {
            if (checkboxes[i].checked) {
                params[params.length] = "id[]=" + checkboxes[i].value;
                checkboxes[i].checked = false;
            }
        }

        if (params.length === 0) {
            alert("Nenhum registro selecionado.");
        } else {
            ajax(url, "table=" + tableId + "&action=" + action + "&" + params.join("&"), function (response, message) {
                alert(message);
            });
        }
    }
}

/**
 * Funções botão para o topo da tela
 * *****************************************************************************
 */
/**
 * Posiciona no topo da página
 */
function topScroll() {
    document.body.scrollTop = 0;
    document.documentElement.scrollTop = 0;
}

/**
 * Funções de teclado de entrada
 * *****************************************************************************
 */
/**
 * Teclado numérico
 * 
 * @param {element} input "onclick='numericKeyboard(this)'"
 */
function numericKeyboard(input) {

    var keyboard = document.getElementById(input.id + "_keyboard");

    if (keyboard.innerHTML === "") {

        var table = document.createElement("TABLE");

        tr = document.createElement("TR");
        tr.innerHTML = "<td onclick=\"setNumericValue('" + input.id + "', '7')\">7</td><td onclick=\"setNumericValue('" + input.id + "', '8')\">8</td><td onclick=\"setNumericValue('" + input.id + "', '9')\">9</td>";
        table.appendChild(tr);

        tr = document.createElement("TR");
        tr.innerHTML = "<td onclick=\"setNumericValue('" + input.id + "', '4')\">4</td><td onclick=\"setNumericValue('" + input.id + "', '5')\">5</td><td onclick=\"setNumericValue('" + input.id + "', '6')\">6</td>";
        table.appendChild(tr);

        tr = document.createElement("TR");
        tr.innerHTML = "<td onclick=\"setNumericValue('" + input.id + "', '1')\">1</td><td onclick=\"setNumericValue('" + input.id + "', '2')\">2</td><td onclick=\"setNumericValue('" + input.id + "', '3')\">3</td>";
        table.appendChild(tr);

        tr = document.createElement("TR");
        tr.innerHTML = "<td colspan=\"2\" onclick=\"setNumericValue('" + input.id + "', '+/-')\">+/-</td><td onclick=\"setNumericValue('" + input.id + "', '0')\">0</td>";
        table.appendChild(tr);

        tr = document.createElement("TR");
        tr.innerHTML = "<td onclick=\"setNumericValue('" + input.id + "', 'limpar')\">&#10006;</td><td onclick=\"setNumericValue('" + input.id + "', 'voltar')\">&#9756;</td><td onclick=\"setNumericValue('" + input.id + "', 'ok')\">&#10004;</td>";
        table.appendChild(tr);

        keyboard.appendChild(table);

        var digits = document.createElement("INPUT");
        digits.id = input.id + "_digits";
        digits.type = "hidden";

        keyboard.appendChild(digits);
    }

    if (keyboard.style.display === "block") {
        keyboard.style.display = "none";
    } else {
        keyboard.style.display = "block";
    }
}

/**
 * Seta valor no input via teclado numérico
 * 
 * @param {string} inputId
 * @param {string} value 
 */
function setNumericValue(inputId, value) {

    var input = document.getElementById(inputId);
    var digits = document.getElementById(input.id + "_digits");
    var keyboard = document.getElementById(input.id + "_keyboard");

    if (value === "+/-") {
        input.value = input.value.substring(0, 1) === "-" ? input.value.substring(1) : "-" + input.value;
    } else if (value === "limpar") {
        digits.value = "";
        input.value = "";
        keyboard.style.display = "none";
    } else if (value === "voltar") {
        digits.value = digits.value.substring(0, digits.value.length - 1);
        var num = parseFloat(digits.value);
        input.value = num.format();
    } else if (value === "ok") {
        keyboard.style.display = "none";
    } else {
        if (input.value.length > 17) {
            return;
        } else {
            digits.value += value;
            var num = parseFloat(digits.value);
            input.value = num.format();
        }
    }
}

/**
 * Configura eventos de inicialização
 * *****************************************************************************
 */
window.addEventListener("scroll", function () {

    var button = document.getElementById("top_button");

    if (button !== null) {
        if (document.body.scrollTop > 20 || document.documentElement.scrollTop > 20) {
            button.style.display = "block";
        } else {
            button.style.display = "none";
        }
    }
});

window.addEventListener("load", function () {

    var spinner = document.getElementById("spinner");

    if (spinner !== null) {
        spinner.style.display = "none";
    }
});
