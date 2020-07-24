function initToasts() {
    $('#errorToast').toast({
        'delay': 10000
    })
}

function showErrorToast(message) {
    document.getElementById('errorToastBody').innerText = message
    $('#errorToast').toast('show')
}