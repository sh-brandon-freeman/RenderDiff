var isLoadComplete = (function() {
    var oldSend = XMLHttpRequest.prototype.send,
        currentRequests = [];

    XMLHttpRequest.prototype.send = function() {
        currentRequests.push(this); // add this request to the stack
        oldSend.apply(this, arguments); // run the original function

        // add an event listener to remove the object from the array
        // when the request is complete
        this.addEventListener('readystatechange', function() {
            var idx;

            if (this.readyState === XMLHttpRequest.DONE) {
                idx = currentRequests.indexOf(this);
                if (idx > -1) {
                    currentRequests.splice(idx, 1);
                }
            }
        }, false);
    };

    return function() {
        return currentRequests == 0;
    }
}());
