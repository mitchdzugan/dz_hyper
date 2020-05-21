const { init, onKeyDown } = require('./dist/core');

module.exports = {
	decorateNotification: (C, { React }) => {
		return class extends React.Component {
			render () {
				const splits = (this.props.text || "").split("x");
				const isDims = splits.length === 2 && (
					splits.every(s => !Number.isNaN(Number(s)))
				);

				if (isDims && window.justToggled) {
					window.justToggled = false;
					return null;
				}

				return React.createElement(C, this.props);
			}
		};
	},

	decorateTerms: (C, { React }) => {
		return class extends React.Component {
			constructor(props) {
				super(props);
				this.off = () => {};
			}

			componentDidMount () {
				this.off = init(
					this.containerRef,
					() => { window.isOpen = true;  window.justToggled = true; },
					() => { window.isOpen = false; window.justToggled = true; }
				);
				window.addEventListener(
					'keydown',
					(e) => {
						onKeyDown(e);
						if (window.isOpen) {
							e.preventDefault();
							e.stopPropagation();
						}
					},
					{ capture: true }
				);
			}

			componentWillUnmount () {
				this.off();
				this.off = () => {};
			}

			render () {
				return React.createElement(
					"div",
					{
						style: {
							display: "flex",
							flexDirection: "column",
							height: "100vh",
							position: "relative"
						}
					},
					[
						React.createElement(
							"div",
							{
								style:{
									flex: "1",
									position: "relative"
								}
							},
							React.createElement(
								"div",
								{
									style: {
										position: "absolute",
										height: "100%",
										width: "100%",
										left: "0",
										top: "0"
									}
								},
								React.createElement(C, this.props),
							),
						),
						React.createElement(
							"div",
							{
								ref: (r) => {
									if (r) {
										this.containerRef = r;
									}
								}
							}
						)
					]
				);
			}
		};
	}
};
