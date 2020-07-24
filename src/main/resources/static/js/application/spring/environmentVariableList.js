const rowTemplate =
    `<div class="row">
      <div class="col">
        <div class="input-group mb-3 envVariable">
          <input type="text" class="form-control" placeholder="Variable name" name="envVariableName">
          <input type="text" class="form-control" placeholder="Variable value" name="envVariableValue">
          <div class="input-group-append">
            <button class="btn btn-outline-danger" onclick="deleteButtonHandler(this)">Delete</button>
          </div>
        </div>
      </div>
    </div>`

function addRow() {
    const listDiv = document.getElementById("env")
    const template = document.createElement('template')
    template.innerHTML = rowTemplate
    listDiv.appendChild(template.content)

    console.log($('.toast'))
}

function deleteButtonHandler(button) {
    const toDelete = button.parentNode.parentNode.parentNode.parentNode //ugh
    const listDiv = document.getElementById("env")

    listDiv.removeChild(toDelete)
}

function getVariableList() {
    const rows = document.getElementById("env").querySelectorAll('.envVariable')
    return Array.from(rows).map((node, i) => {
        return {
            'name': node.querySelector('[name=envVariableName]').value,
            'value': node.querySelector('[name=envVariableValue]').value
        }
    }).filter(variable => variable['name'].length > 0)
}