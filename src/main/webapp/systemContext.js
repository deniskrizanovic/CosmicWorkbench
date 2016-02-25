function getListofSystemContexts() {

    return [
        {id: 1, name: "Aon Trading Platform"},
        {id: 2, name: "ODS"},
        {id: 3, name: "OCS"},
        {id: 4, name: "Another One"},
        {id: 5, name: "And One More"}
    ];


}


var systemContextList = {
    view: "list",
    width: 320,
    height: 500,
    select: true,
    template: "#name#",
    on: {
        onAfterSelect: function (listIndex, e) {
            webix.message("");
        }
    }
};

var systemContextWithHeader = {
    rows: [
        {template: "<b>System Contexts</b>", height: 35},
        systemContextList
    ]
};




