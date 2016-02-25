
function getListOfFunctionalProcesses() {

    return [
        {id: 1, name: "1FuncProce10"},
        {id: 2, name: "2FuncProce20"},
        {id: 3, name: "3FuncProce30"},
        {id: 4, name: "4FuncProce40"},
        {id: 5, name: "5FuncProce50"}
    ];

}


var fpList = {
    view: "list",
    width: 320,
    height: 500,
    select: true,
    template: "#name#",
    header: "header",
    on: {
        onAfterSelect: function (listIndex, e) {

            var fpName = this.getItem(listIndex).name;
            refreshDataTableWithNewFunctionalProcess(fpName);

        }
    }
};

var fpListWithHeader = {
    rows: [
        {template: "<b>Functional Processes</b>", height: 35},
        fpList
    ]
};




