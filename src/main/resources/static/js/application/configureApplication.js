async function saveDefaultEnvironment(appId) {
  const csrfHeaderValue = document.querySelector("meta[name='_csrf']").getAttribute('content')


  const response = await fetch(`/api/env/application/${appId}`, {
    method: "POST",
    credentials: "same-origin",
    headers: {
      'Content-Type': 'application/json',
      'X-CSRF-TOKEN': csrfHeaderValue
    },
    body: JSON.stringify({'variables': getVariableList()})
  })
  console.log(JSON.stringify({'variables': getVariableList()}))
  console.log(response)
  console.log(await response.text())
  if(response.status === 200 ) window.location.href = `/app/${appId}`
  else showErrorToast(response.status)

}
window.onload = function(){
  initToasts()
}
