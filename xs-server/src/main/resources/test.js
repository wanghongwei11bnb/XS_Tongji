const prize = {
    1001: {title: ''},
    1002: {title: ''},
    1003: {title: ''},
    1004: {title: ''},
    1005: {title: ''},
    2001: {title: ''},
};

class ChanceCursor {
    constructor() {
        this.marker = 0;
        this.random = Math.random();
    }

    reset() {
        this.marker = 0;
    }

    match(chance) {
        let prev_marker = this.marker;
        this.marker += chance;
        return prev_marker <= random <= this.marker;
    }

}





