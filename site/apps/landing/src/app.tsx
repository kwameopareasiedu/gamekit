const showcaseItems: { name: string; src: string; href: string }[] = [
  {
    name: "Industrio",
    src: "/industrio.png",
    href: "https://kwameopareasiedu.itch.io/industrio",
  },
  {
    name: "Sokoban (Clone)",
    src: "/sokoban.png",
    href: "https://github.com/kwameopareasiedu/gamekit-sokoban",
  },
  {
    name: "Sliding Puzzle (Clone)",
    src: "/sliding-puzzle.png",
    href: "https://github.com/kwameopareasiedu/gamekit-sliding-puzzle",
  },
];

export function App() {
  return (
    <>
      <div className="fixed top-0 left-0 w-screen h-screen -z-10 bg-gradient-to-br from-[#0C213A] to-[#004B4F]" />

      <div className="min-h-screen py-16 md:py-24 px-6 [&_*]:text-white">
        <a
          target="_blank"
          href="https://github.com/kwameopareasiedu/gamekit"
          className="fixed text-center w-full bg-gradient-to-b from-red-500 to-red-900 p-2 top-10 md:top-7 right-14 md:right-11 rotate-[45deg] translate-x-1/2 z-10">
          <div className="border-t-[1px] border-b-[1px] border-dashed md:text-sm">Star On Github</div>
        </a>

        <div className="container mx-auto">
          <p className="text-center text-sm tracking-[0.15em]">Introducing</p>
          <h1 className="text-center text-7xl py-4 font-bold">GameKit</h1>

          <div className="flex items-center justify-center gap-2">
            <a href="https://docs.gamekit.opare.dev" className="link" target="_blank">
              Get Started
            </a>

            <a
              href="https://github.com/kwameopareasiedu/gamekit/tree/master/samples/src/main/java"
              className="link"
              target="_blank">
              Samples
            </a>
          </div>
        </div>

        <section data-label="/ about" className="mt-8">
          <p>GameKit is a 2D Java game engine for creating simple games fast!</p>

          <p>It’s built using Java Swing and doesn't use OpenGL hence rendering is CPU based and not GPU based.</p>

          <p>
            GameKit is in no way a AAA engine and has its limitations, but performance is decent enough for small to
            medium-sized games.
          </p>
        </section>

        <section data-label="/ showcase" className="mt-8">
          <div className="overflow-auto !whitespace-nowrap space-x-4 custom-scrollbar">
            {showcaseItems.map(({ name, src, href }) => (
              <a className="showcase-item" key={name} href={href} target="_blank">
                <div className="img">
                  <img src={src} alt={name} />
                </div>

                <p>{name}</p>
              </a>
            ))}
          </div>
        </section>

        <section data-label="/ license" className="mt-8">
          The GameKit engine is free and open-source and distributed under the MIT license. However you are free to
          license games created with the engine anyway you want.
        </section>

        <footer className="fixed bottom-0 left-0 w-full bg-black z-10 py-2.5">
          <p className="text-center">GameKit, {new Date().getFullYear()}</p>
        </footer>
      </div>
    </>
  );
}
