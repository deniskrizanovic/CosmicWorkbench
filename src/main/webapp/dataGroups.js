
function getListofDataGroups() {

    return [
        {id: 1, name: "Placement Base"},
        {id: 2, name: "ODS Client Base"},
        {id: 3, name: "ODS Client Address"},
        {id: 4, name: "Policy Cover Base"},
        {id: 5, name: "Policy Cover Cover Numbers"}
    ];
}

var dataGroupList = {
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

var dataGroupListWithHeader = {
    rows: [
        {template: "<b>Data Groups</b>", height: 35},
        dataGroupList
    ]
};

//var dataGroupWindow =   {cols: [toolbar, dataGroupListWithHeader, {view: "resizer"}, dataGridBody]};




