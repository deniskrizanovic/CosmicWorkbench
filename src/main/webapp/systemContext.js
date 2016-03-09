function getListofSystemContexts() {

    return [
        {id: 1, name: "Aon Trading Platform"},
        {id: 2, name: "ODS"},
        {id: 3, name: "OCS"},
        {id: 4, name: "Another One"},
        {id: 5, name: "And One More"}
    ];


};


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

var systemContextDetails = {
    id: "systemContextDetailsForm",
    view: "form",
    elements: [
        {view: "text", label: "Name"},
        {view: "text", label: "Description"},
        {
            margin: 5, cols: [
            {view: "button", value: "Save", type: "form"},
            {view: "button", value: "Cancel"}
        ]
        }
    ]

};

var activityList = {
    id: "activityList",
    view: "list",
    width: 320,
    height: 500,
    select: true,
    template: "#title#",
    data: [
        {id: 1, title: "Item 1"},
        {id: 2, title: "Item 2"},
        {id: 3, title: "Item 3"}
    ]


};

var systemContextBody = {
    view: "tabview",
    cells: [
        {
            header: "Details",
            body: systemContextDetails

        },
        {
            header: "Activity",
            body: activityList
        }
    ]
};



