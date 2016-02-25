function getListOfSizingScenarios() {

    return [
        {id: 1, name: "RS-2845"},
        {id: 2, name: "RS-1234"},
        {id: 3, name: "RS-7483"},
        {id: 4, name: "RS-7823"},
        {id: 5, name: "RS-5790"}
    ];

}


var scenarioList = {
    view: "list",
    width: 320,
    height: 500,
    select: true,
    template: "#name#",
    on: {
        onAfterSelect: function (listIndex, e) {

            webix.message("I selected a scenario")

        }
    }
};

var scenarioListWithHeader = {
    rows: [
        {template: "<b>Sizing Scenarios</b>", height: 35},
        scenarioList
    ]
};




